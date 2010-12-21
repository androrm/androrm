/**
 * 	Copyright (c) 2010 Philipp Giese
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.orm.androrm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

/**
 * @author Philipp GIese
 *
 */
public class QueryBuilder {
	
	private static final String TAG = "ANDRORM:QUERY:BUILDER";
	
	public static final <T extends Model> SelectStatement buildQuery(Class<T> clazz, 
			List<Filter> filters,
			int depth) 
	throws NoSuchFieldException {
		
		String tableName = Model.getTableName(clazz);
		
		JoinStatement selfJoin = new JoinStatement();
		selfJoin.left(tableName, "self" + depth);
		
		SelectStatement subSelect = new SelectStatement();
		Filter filter = filters.get(0);
		
		List<String> fields = Arrays.asList(filter.getKey().split("__"));
		
		if(fields.size() == 1) {
			String fieldName = fields.get(0);
			
			T instance = null;
			
			try {
				Constructor<T> constructor = clazz.getConstructor();
				instance = constructor.newInstance();
			} catch(Exception e) {
				Log.e(TAG, "exception thrown while trying to create representation of " 
						+ clazz.getSimpleName() 
						+ " and fetching field object for field " 
						+ fieldName, e);
			}
			
			if(instance != null) {
				Field field = Model.getField(clazz, instance, fieldName);
				
				if(field != null) {
					Object o = null;
					
					try {
						o = field.get(instance);
					} catch(IllegalAccessException e) {
						Log.e(TAG, "exception thrown while trying to create representation of " 
								+ clazz.getSimpleName() 
								+ " and fetching field object for field " 
								+ fieldName, e);
					}
					
					if(isRelationalField(o)) {
						// gather ids for fields
						SelectStatement s = buildJoin(clazz, fields, filter, depth);
						
						JoinStatement join = new JoinStatement();
						join.left(tableName, "a")
							.right(s, "b")
							.on(Model.PK, tableName);
						
						subSelect.from(join)
								 .select("a.*");
					} else {
						Where where = new Where();
						where.setStatement(filter.getStatement());
						
						subSelect.from(tableName)
							  	 .where(where);
					}
				}
			}
		} else {
			int left = depth + 1;
			int right = depth + 2;
			
			JoinStatement join = new JoinStatement();
			join.left(tableName, "outer" + left)
				.right(buildJoin(clazz, 
						fields, 
						filter, 
						depth), "outer" + right)
				.on(Model.PK, tableName);
			
			subSelect.from(join)
				  	 .select("outer" + left + ".*");
		}
		
		if(filters.size() == 1) {
			return subSelect;
		}
		
		selfJoin.right(subSelect, "self" + (depth + 1))
				.on(Model.PK, Model.PK);
		
		JoinStatement outerSelfJoin = new JoinStatement();
		outerSelfJoin.left(subSelect, "outerSelf" + depth)
					 .right(buildQuery(clazz, 
							 filters.subList(1, 
									 filters.size()), 
							 (depth + 2)), 
							 "outerSelf" + (depth + 1))
					 .on(Model.PK, Model.PK);
		
		SelectStatement select = new SelectStatement();
		select.from(outerSelfJoin)
			  .select("outerSelf" + depth + ".*");
		
		return select;
		
	}
	
	@SuppressWarnings("unchecked")
	private static final <T extends Model> void unwrapManyToManyRelation(Map<String, String> joinParams, 
			Class<T> clazz, 
			Relation r) {
		
		ManyToManyField<T, ? extends Model> m = (ManyToManyField<T, ? extends Model>) r;
		Class<? extends Model> target = m.getTarget();
		
		/*
		 * As ManyToManyFields are represented in a separate
		 * relation table this table has to be considered for
		 * the join.
		 */
		joinParams.put("leftTable", m.getRelationTableName());
		/*
		 * By convention the fields in a relation table are named
		 * after the classes they represent. Thus we select the field
		 * with the name of the class we are currently examining.
		 */
		String selectField = Model.getTableName(clazz);
		joinParams.put("selectField", Model.getTableName(clazz));
		/*
		 * When dealing with foreign keys the selection field has
		 * to be renamed to the field name in the representing class.
		 * ManyToManyFields do not need this.
		 */
		joinParams.put("selectAs", selectField);
		/*
		 * Field of the left table that will be considered 
		 * during the join. 
		 */
		joinParams.put("onLeft", Model.getTableName(target));
	}
	
	private static final <T extends Model> void unwrapForeignKeyRelation(Map<String, String> joinParams,
			String fieldName,
			Class<T> clazz,
			Relation r) {
		
		/*
		 * As ForeignKeyFields are real fields in the
		 * database our left table for the join
		 * is the table corresponding to the class,
		 * we are currently examining.
		 */
		String leftTable = Model.getTableName(clazz);
		joinParams.put("leftTable", leftTable);
		/*
		 * As we do not operate on a relation table
		 * we need to select the id field of our 
		 * current table. 
		 */
		joinParams.put("selectField", Model.PK);
		/*
		 * In order to work along with ManyToManyFields
		 * we select the Id field as the table name
		 * corresponding to our class. 
		 */
		joinParams.put("selectAs", leftTable);
		
		/*
		 * Field of the left table, that will be considered
		 * during the join.
		 */
		joinParams.put("onLeft", fieldName);
	}
	
	private static final <T extends Model> void unwrapOneToManyField(Map<String, String> joinParams, 
			String fieldName,
			Class<T> clazz,
			Relation r) {
		
		Class<? extends Model> target = r.getTarget();
		
		/*
		 * One to Many fields have no field representation in their origin 
		 * class. Therefore we must determine the target class for the join.
		 */
		joinParams.put("leftTable", Model.getTableName(target));
		
		/*
		 * On the target class we select the field pointing back to 
		 * the origin class
		 */
		joinParams.put("selectField", Model.getBackLinkFieldName(target, clazz));
		
		/*
		 * This field has to be selected under the alias of the origin class.
		 */
		joinParams.put("selectAs", Model.getTableName(clazz));
		
		/*
		 * We have to join over the primary key of the target class,
		 * as this is our indirect reference
		 */
		joinParams.put("onLeft", Model.PK);
	}
	
	protected static final boolean isDatabaseField(Object field) {
		if(field != null) {
			if(field instanceof DataField
					|| isRelationalField(field)) {
				
				return true;
			}
		}
		
		return false;
	}
	
	protected static final boolean isRelationalField(Object field) {
		if(field != null) {
			if(field instanceof ForeignKeyField
					|| field instanceof OneToManyField
					|| field instanceof ManyToManyField) {
				
				return true;
			}
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private static final <T extends Model> SelectStatement getRelationSelection(Relation r, Class<T> clazz, Filter filter) {
		if(r instanceof ManyToManyField) {
			ManyToManyField<T, ?> m = (ManyToManyField<T, ?>) r;
			Class<? extends Model> target = r.getTarget();
			
			Statement stmt = filter.getStatement();
			stmt.setKey(Model.getTableName(target));
			
			Where where = new Where();
			where.setStatement(stmt);
			
			SelectStatement s = new SelectStatement();
			s.from(m.getRelationTableName())
			 .distinct()
			 .select(Model.getTableName(clazz))
			 .where(where);
			
			return s;
		} else if(r instanceof OneToManyField) {
			Class<? extends Model> target = r.getTarget();
			
			String backLinkFieldName = Model.getBackLinkFieldName(target, clazz);
			
			Statement stmt = filter.getStatement();
			stmt.setKey(backLinkFieldName);
			
			Where where = new Where();
			where.setStatement(stmt);
			
			SelectStatement s = new SelectStatement();
			s.from(Model.getTableName(target))
			 .distinct()
			 .select(backLinkFieldName + " AS " + Model.getTableName(clazz))
			 .where(where);
			
			return s;
		}	
		
		return null;
	}
	
	public static final <T extends Model> SelectStatement buildJoin(Class<T> clazz,
			List<String> fields, 
			Filter filter, 
			int depth) throws NoSuchFieldException {
		
		T instance = null;
		String fieldName = fields.get(0);
		
		try {
			Constructor<T> constructor = clazz.getConstructor();
			instance = constructor.newInstance();
		} catch(Exception e) {
			Log.e(TAG, "exception thrown while trying to create representation of " 
					+ clazz.getSimpleName() 
					+ " and fetching field object for field " 
					+ fieldName, e);
		}
		
		SelectStatement select = new SelectStatement();
		
		if(instance != null) {
		
			Field field = Model.getField(clazz, instance, fieldName);
			
			if(field == null) {
				throw new NoSuchFieldException("Could not resolve " 
						+ fieldName 
						+ " into class "
						+ clazz.getSimpleName()
						+ ". Choices are: " + 
						Model.getEligableFields(clazz, instance).toString());
			} else {
				Object o = null;
				
				try {
					o = field.get(instance);
				} catch(IllegalAccessException e) {
					Log.e(TAG, "exception thrown while trying to create representation of " 
							+ clazz.getSimpleName() 
							+ " and fetching field object for field " 
							+ fieldName, e);
				}
				
				if(isDatabaseField(o)) {
					if(fields.size() == 1) {
						if(isRelationalField(o) 
								&& !(o instanceof ForeignKeyField)) {
							
							Relation r = (Relation) o;
							
							return getRelationSelection(r, clazz, filter);
						} else {
							String tableName = Model.getTableName(clazz);
							
							Where where = new Where();
							where.setStatement(filter.getStatement());
							
							select.from(tableName)
								  .distinct()
								  .select(Model.PK + " AS " + tableName)
								  .where(where);
							
							return select;
						}
					} 
					
					if(isRelationalField(o)) {
						Relation r = (Relation) o;
						
						Class<? extends Model> target = r.getTarget();
						
						Map<String, String> joinParams = new HashMap<String, String>();
						
						if(r instanceof ManyToManyField) {
							unwrapManyToManyRelation(joinParams, clazz, r);
						}
						
						if(r instanceof ForeignKeyField) {
							unwrapForeignKeyRelation(joinParams, fieldName, clazz, r);
						}
						
						if(r instanceof OneToManyField) {
							unwrapOneToManyField(joinParams, fieldName, clazz, r);
						}
						
						String leftTable = joinParams.get("leftTable");
						String selectField = joinParams.get("selectField");
						String selectAs = joinParams.get("selectAs");
						String onLeft = joinParams.get("onLeft");
						String onRight = Model.getTableName(target);
						
						/*
						 * After the steps above the left side of the join is always known. 
						 * What is currently unknown is, if there are any further sub-joins
						 * needed in order to accomplish the query. Therefore the right side
						 * of the join is provided with the result of this function. 
						 */
						JoinStatement join = new JoinStatement();
						join.left(leftTable, "table" + depth)
							.right(buildJoin(target, 
									fields.subList(1, fields.size()), 
									filter, 
									depth + 2), 
									"table" + (depth + 1))
							.on(onLeft, onRight);
						
						/*
						 * The select will fetch the correct field from the previous join
						 * that will be needed in the next step. 
						 */
						select.from(join)
							  .distinct()
						 	  .select("table" 
						 			  + depth 
						 			  + "."
						 			  + selectField
						 			  + " AS "
						 			  + selectAs);
					}
				}
			}
		}
		
		return select;
	}
}
