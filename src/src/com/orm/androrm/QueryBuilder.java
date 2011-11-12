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
	
	private static final <T extends Model> SelectStatement buildJoin(
			
			Class<T> 		clazz,
			List<String> 	fields, 
			Rule 			filter, 
			int 			depth
			
	) {
		
		T instance = Model.getInstace(clazz);
		
		if(instance != null) {
			Object fieldInstance = getFieldInstance(clazz, instance, fields.get(0));
			
			if(fields.size() == 1) {
				return resolveLastField(fieldInstance, clazz, filter);
			} 
			
			if(DatabaseBuilder.isRelationalField(fieldInstance)) {
				return resolveRelationField(fieldInstance, clazz, fields, filter, depth);
			}
		}
		
		return null;
	}
	
	public static final <T extends Model> SelectStatement buildQuery(
			
			Class<T> 	clazz, 
			List<Rule> 	rules
			
	) {
		
		return buildQuery(clazz, rules, 0);
	}
	
	private static final <T extends Model> SelectStatement buildQuery(
			
			Class<T> 	clazz, 
			List<Rule> 	rules,
			int 		depth
			
	)  {
		
		String tableName = DatabaseBuilder.getTableName(clazz);
		
		JoinStatement selfJoin = new JoinStatement();
		selfJoin.left(tableName, "self" + depth);
		
		SelectStatement subSelect = new SelectStatement();
		Rule rule = rules.get(0);
		
		List<String> fields = Arrays.asList(rule.getKey().split("__"));
		
		if(fields.size() == 1) {
			String fieldName = fields.get(0);
			
			T instance = Model.getInstace(clazz);
			
			if(instance != null) {
				Object o = getFieldInstance(clazz, instance, fieldName);
				
				if(DatabaseBuilder.isRelationalField(o)) {
					// gather ids for fields
					SelectStatement s = buildJoin(clazz, fields, rule, depth);
					
					JoinStatement join = new JoinStatement();
					join.left(tableName, "a")
						.right(s, "b")
						.on(Model.PK, tableName);
					
					subSelect.from(join)
							 .select("a.*");
				} else {
					Where where = new Where();
					where.setStatement(rule.getStatement());
					
					subSelect.from(tableName)
						  	 .where(where);
				}
			}
		} else {
			int left = depth + 1;
			int right = depth + 2;
			
			JoinStatement join = new JoinStatement();
			join.left(tableName, "outer" + left)
				.right(buildJoin(clazz, 
						fields, 
						rule, 
						depth), "outer" + right)
				.on(Model.PK, tableName);
			
			subSelect.from(join)
				  	 .select("outer" + left + ".*");
		}
		
		if(rules.size() == 1) {
			return subSelect;
		}
		
		selfJoin.right(subSelect, "self" + (depth + 1))
				.on(Model.PK, Model.PK);
		
		JoinStatement outerSelfJoin = new JoinStatement();
		outerSelfJoin.left(subSelect, "outerSelf" + depth)
					 .right(buildQuery(clazz, 
							 rules.subList(1, 
									 rules.size()), 
							 (depth + 2)), 
							 "outerSelf" + (depth + 1))
					 .on(Model.PK, Model.PK);
		
		SelectStatement select = new SelectStatement();
		select.from(outerSelfJoin)
			  .select("outerSelf" + depth + ".*");
		
		return select;
		
	}
	
	private static final <T extends Model> Object getFieldInstance(
			
			Class<T> 	clazz, 
			T 			instance, 
			String 		fieldName
	
	)  {
		Field field = Model.getField(clazz, instance, fieldName);
		Object fieldInstance = null;
		
		if(field != null) {
			try {
				fieldInstance = field.get(instance);
			} catch(IllegalAccessException e) {
				Log.e(TAG, "exception thrown while trying to create representation of " 
						+ clazz.getSimpleName() 
						+ " and fetching field object for field " 
						+ fieldName, e);
			}
		}
		
		return fieldInstance;
	}
	
	@SuppressWarnings("unchecked")
	private static final <T extends Model> SelectStatement getRelationSelection(
			
			Relation<?> r, 
			Class<T> 	clazz, 
			Rule 		rule
			
	) {
		Class<? extends Model> target = r.getTarget();
		Statement stmt = rule.getStatement();
		Where where = new Where();
		SelectStatement select = new SelectStatement();
		
		if(r instanceof ManyToManyField) {
			ManyToManyField<T, ?> m = (ManyToManyField<T, ?>) r;
			
			stmt.setKey(DatabaseBuilder.getTableName(target));
			
			where.setStatement(stmt);
			
			select.from(m.getRelationTableName())
			 	  .select(DatabaseBuilder.getTableName(clazz));
		} 
		
		if(r instanceof OneToManyField) {
			String backLinkFieldName = Model.getBackLinkFieldName(target, clazz);
			
			stmt.setKey(backLinkFieldName);
			
			where.setStatement(stmt);
			
			select.from(DatabaseBuilder.getTableName(target))
			 	  .select(backLinkFieldName + " AS " + DatabaseBuilder.getTableName(clazz));
		}	
		
		select.where(where)
			  .distinct();
		
		return select;
	}
	
	private static final <T extends Model> SelectStatement resolveLastField(
			
			Object 		field, 
			Class<T> 	clazz, 
			Rule 		rule
			
	) {
		
		SelectStatement select = new SelectStatement();
		
		if(DatabaseBuilder.isRelationalField(field) 
				&& !(field instanceof ForeignKeyField)) {
			
			Relation<?> r = (Relation<?>) field;
			
			return getRelationSelection(r, clazz, rule);
		} 
		
		String tableName = DatabaseBuilder.getTableName(clazz);
		
		Where where = new Where();
		where.setStatement(rule.getStatement());
		
		select.from(tableName)
			  .distinct()
			  .select(Model.PK + " AS " + tableName)
			  .where(where);
		
		return select;
	}
	
	private static final <T extends Model> SelectStatement resolveRelationField(
			
			Object 			field, 
			Class<T> 		clazz, 
			List<String> 	fields, 
			Rule 			rule,
			int 			depth
			
	)  {
		
		Relation<?> r = (Relation<?>) field;
		SelectStatement select = new SelectStatement();
		
		Class<? extends Model> target = r.getTarget();
		
		Map<String, String> joinParams = unwrapRelation(r, fields.get(0), clazz);
		
		String leftTable = joinParams.get("leftTable");
		String selectField = joinParams.get("selectField");
		String selectAs = joinParams.get("selectAs");
		String onLeft = joinParams.get("onLeft");
		String onRight = DatabaseBuilder.getTableName(target);
		
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
					rule, 
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
		
		return select;
	}
	
	private static final <T extends Model> Map<String, String> unwrapForeignKeyRelation(
			
			String 		fieldName,
			Class<T> 	clazz,
			Relation<?> r
			
	) {
		
		Map<String, String> joinParams = new HashMap<String, String>();
		
		/*
		 * As ForeignKeyFields are real fields in the
		 * database our left table for the join
		 * is the table corresponding to the class,
		 * we are currently examining.
		 */
		String leftTable = DatabaseBuilder.getTableName(clazz);
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
		
		return joinParams;
	}
	
	@SuppressWarnings("unchecked")
	private static final <T extends Model> Map<String, String> unwrapManyToManyRelation(
			
			Class<T> 	clazz, 
			Relation<?> r
			
	) {
		
		Map<String, String> joinParams = new HashMap<String, String>();
		ManyToManyField<T, ?> m = (ManyToManyField<T, ?>) r;
		
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
		String selectField = DatabaseBuilder.getTableName(clazz);
		joinParams.put("selectField", selectField);
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
		joinParams.put("onLeft", DatabaseBuilder.getTableName(m.getTarget()));
		
		return joinParams;
	}
	
	private static final <T extends Model> Map<String, String> unwrapOneToManyField(
			
			String 		fieldName,
			Class<T> 	clazz,
			Relation<?> r
			
	) {
		
		Class<? extends Model> target = r.getTarget();
		Map<String, String> joinParams = new HashMap<String, String>();
		
		/*
		 * One to Many fields have no field representation in their origin 
		 * class. Therefore we must determine the target class for the join.
		 */
		joinParams.put("leftTable", DatabaseBuilder.getTableName(target));
		
		/*
		 * On the target class we select the field pointing back to 
		 * the origin class
		 */
		joinParams.put("selectField", Model.getBackLinkFieldName(target, clazz));
		
		/*
		 * This field has to be selected under the alias of the origin class.
		 */
		joinParams.put("selectAs", DatabaseBuilder.getTableName(clazz));
		
		/*
		 * We have to join over the primary key of the target class,
		 * as this is our indirect reference
		 */
		joinParams.put("onLeft", Model.PK);
		
		return joinParams;
	}
	
	private static final <T extends Model> Map<String, String> unwrapRelation(
			
			Relation<?> r, 
			String 		fieldName,
			Class<T> 	clazz
			
	) {
		
		if(r instanceof ManyToManyField) {
			return unwrapManyToManyRelation(clazz, r);
		} else if(r instanceof ForeignKeyField) {
			return unwrapForeignKeyRelation(fieldName, clazz, r);
		} else if(r instanceof OneToManyField) {
			return unwrapOneToManyField(fieldName, clazz, r);
		}
		
		return null;
	}
}
