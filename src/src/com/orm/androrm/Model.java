/**
 * 
 */
package com.orm.androrm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;


/**
 * @author Philipp Giese
 *
 */
public abstract class Model {
	
	private static final String TAG = "ANDORM:MODEL";
	
	protected static final String PK = "mId";
	
	protected static final String getTableName(Class<?> clazz) {
		return clazz.getSimpleName().toLowerCase();
	}
	
	private static final String MODIFIER_IN = "in";
	private static final String MODIFIER_CONTAINS = "contains";
	private static final String MODIFIER_EXACT = "is";
	
	private static final List<String> MODIFIERS = Arrays.asList(new String[] {
			MODIFIER_IN,
			MODIFIER_CONTAINS,
			MODIFIER_EXACT
	});
	
	/**
	 * Assigns a value gathered from the database to the
	 * instance <code>object</b> of type T. Due to the nature
	 * of this ORM only fields applicable for serialization
	 * will be considered.
	 * 
	 * @param <T>		Type of the object.
	 * @param field		Field of the object, that a value shall be assigned to.
	 * @param object	Object instance of type T.
	 * @param c			Database {@link Cursor}
	 * 
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private static final <T extends Model> void assignFieldValue(Field field, 
			T object,
			Cursor c) 
	throws IllegalArgumentException, IllegalAccessException {
		
		String fieldName = field.getName();
		
		Class<?> fieldType = field.getType();
		if(fieldType.equals(IntegerField.class)
				|| fieldType.equals(PrimaryKeyField.class)) {
			IntegerField integerField = (IntegerField) field.get(object);
			integerField.set(c.getInt(c.getColumnIndexOrThrow(fieldName)));
		}
		
		if(fieldType.equals(CharField.class)) {
			CharField charField = (CharField) field.get(object);
			charField.set(c.getString(c.getColumnIndexOrThrow(fieldName)));
		}
		
		if(fieldType.equals(ForeignKeyField.class)) {
			ForeignKeyField<?> foreignKeyField = (ForeignKeyField<?>) field.get(object);
			foreignKeyField.set(c.getInt(c.getColumnIndexOrThrow(fieldName)));
		}
	}
	
	protected static final <T extends Model> void createObject(T object, 
			Class<?> clazz,
			Cursor c) 
	throws IllegalArgumentException, IllegalAccessException {
		
		if(clazz != null && clazz.isInstance(object) && !clazz.equals(Object.class)) {
			Field[] fields = clazz.getDeclaredFields();
			
			for(int i = 0, length = fields.length; i < length; i++) {
				Field field = fields[i];
				field.setAccessible(true);
				
				assignFieldValue(field, object, c);
			}
			
			createObject(object, clazz.getSuperclass(), c);
		}
	}
	
	protected static final <T extends Model> T get(Context context, Class<T> clazz, int id) {
		T object = null;
		
		try {
			Constructor<T> constructor = clazz.getConstructor(Context.class);
			object = constructor.newInstance(context);
			
			Where where = new Where();
			where.and(PK, id);
			
			DatabaseAdapter adapter = new DatabaseAdapter(context);
			adapter.open();
			
			Cursor c = adapter.get(getTableName(clazz), where, null);
			
			if(c.moveToNext()) {
				createObject(object, clazz, c);
			}
			
		} catch(Exception e) {
			Log.e(TAG, "exception thrown while gathering representation for object of class " 
					+ clazz.getSimpleName() 
					+ " with id " 
					+ id, e);
		}
		
		return object;
	}
	
	private static final boolean isDatabaseField(Object field) {
		if(field != null) {
			if((field instanceof DataField)
					|| (field instanceof ManyToManyField)
					|| field instanceof ForeignKeyField) {
				
				return true;
			}
		}
		
		return false;
	}
	
	private static final boolean isRelationalField(Object field) {
		if(field != null) {
			if(field instanceof ForeignKeyField
					|| field instanceof ManyToManyField) {
				
				return true;
			}
		}
		
		return false;
	}
	
	private static final List<String> getEligableFields(Class<?> clazz) {
		List<String> eligableFields = new ArrayList<String>();
		
		if(!clazz.equals(Object.class)) {
			Field[] fields = clazz.getDeclaredFields();
			
			for(int i = 0, length = fields.length; i < length; i++) {
				Field field = fields[i];
				field.setAccessible(true);
				Class<?> type = field.getType();
			
				if(type.equals(IntegerField.class)
						|| type.equals(CharField.class)
						|| type.equals(ForeignKeyField.class)
						|| type.equals(ManyToManyField.class)) {
					
					eligableFields.add(field.getName());
				}
			}
			
			eligableFields.addAll(getEligableFields(clazz.getSuperclass()));
		}
		
		return eligableFields;
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
		String selectField = getTableName(clazz);
		joinParams.put("selectField", getTableName(clazz));
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
		String onLeft = getTableName(target);
		joinParams.put("onLeft", getTableName(target));
		/*
		 * Field of the right table that will be matched 
		 * against the field of the left table during the join.
		 */
		joinParams.put("onRight", onLeft);
	}
	
	@SuppressWarnings("unchecked")
	private static final <T extends Model> void unwrapForeignKeyRelation(Map<String, String> joinParams,
			String fieldName,
			Class<T> clazz,
			Relation r) {
		
		ForeignKeyField<? extends Model> f = (ForeignKeyField<? extends Model>) r;
		Class<? extends Model> target = f.getTarget();
		
		/*
		 * As ForeignKeyFields are real fields in the
		 * database our left table for the join
		 * is the table corresponding to the class,
		 * we are currently examining.
		 */
		String leftTable = getTableName(clazz);
		joinParams.put("leftTable", getTableName(clazz));
		/*
		 * As we do not operate on a relation table
		 * we need to select the id field of our 
		 * current table. 
		 */
		joinParams.put("selectField", PK);
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
		/*
		 * Field of the right table, that will be matched
		 * against the field of the left table during the join.
		 */
		joinParams.put("onRight", getTableName(target));
	}
	
	private static final <T extends Model> SelectStatement buildJoin(Context context, 
			Class<T> clazz,
			List<String> fields, 
			String value, 
			int depth) throws NoSuchFieldException {
		
		SelectStatement select = new SelectStatement();
		String fieldName = fields.get(0);
		Field field = getField(clazz, fieldName);
		
		if(field == null) {
			throw new NoSuchFieldException("Could not resolve " 
					+ fieldName 
					+ " into class "
					+ clazz.getSimpleName()
					+ ". Choices are: " + 
					getEligableFields(clazz).toString());
		} else {
			Object o = null;
			
			try {
			
				Constructor<T> constructor = clazz.getConstructor(Context.class);
				T object = constructor.newInstance(context);
			
				o = field.get(object);
			} catch(Exception e) {
				Log.e(TAG, "exception thrown while trying to create representation of " 
						+ clazz.getSimpleName() 
						+ " and fetching field object for field " 
						+ fieldName, e);
			}
			
			if(isDatabaseField(o)) {
				if(fields.size() == 1) {
					String tableName = getTableName(clazz);
					
					Where where = new Where();
					where.and(fieldName, value);
					
					select.from(tableName)
						  .select(new String[] {PK + " AS " + tableName})
						  .where(where);
					
					return select;
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
					
					String leftTable = joinParams.get("leftTable");
					String selectField = joinParams.get("selectField");
					String selectAs = joinParams.get("selectAs");
					String onLeft = joinParams.get("onLeft");
					String onRight = joinParams.get("onRight");
					
					/*
					 * After the steps above the left side of the join is always known. 
					 * What is currently unknown is, if there are any further sub-joins
					 * needed in order to accomplish the query. Therefore the right side
					 * of the join is provided with the result of this function. 
					 */
					JoinStatement join = new JoinStatement();
					join.left(leftTable, "table" + depth)
						.right(buildJoin(context, 
								target, 
								fields.subList(1, fields.size()), 
								value, 
								depth + 2), 
								"table" + (depth + 1))
						.on(onLeft, onRight);
					
					/*
					 * The select will fetch the correct field from the previous join
					 * that will be needed in the next step. 
					 */
					select.from(join)
					 	  .select(new String[] {"table" 
					 			  + depth 
					 			  + "."
					 			  + selectField
					 			  + " AS "
					 			  + selectAs});
				}
			}
		}
		
		return select;
		
	}
	
	private static final Field getField(Class<?> clazz, String fieldName) {
		Field field = null;
		
		if(!clazz.equals(Object.class)) {
		
			Field[] fields = clazz.getDeclaredFields();
			
			for(int i = 0, length = fields.length; i < length; i++) {
				Field f = fields[i];
				f.setAccessible(true);
				
				if(f.getName().equals(fieldName)) {
					field = f;
					break;
				}
			}
			
			if(field == null) {
				field = getField(clazz.getSuperclass(), fieldName);
			}
		}
		
		return field;
	}
	
	@SuppressWarnings("unchecked")
	private static final <T extends Model> List<String> getTableNames(Context context, 
			Class<T> clazz, 
			List<String> fields,
			int depth) {
		
		List<String> tables = new ArrayList<String>();
		String fieldName = fields.get(depth);
		
		try {
			Constructor<T> constructor = clazz.getConstructor(Context.class);
			T object = constructor.newInstance(context);
			Field field = getField(clazz, fieldName);
			Object o = field.get(object);
			
			if(o instanceof ManyToManyField) {
				ManyToManyField<T, ? extends Model> m = (ManyToManyField<T, ? extends Model>) o;
				Class<? extends Model> target = m.getTarget();
				
				if(fields.size() == 2) {
					tables.add(getTableName(target));
					
					return tables;
				}
				
				tables.add(m.getRelationTableName());
				tables.addAll(getTableNames(context, target, fields, depth + 1));
			}
		} catch(Exception e) {
			//discard
		}
		
		return tables;
	}
	
	private static final <T extends Model> SelectStatement buildQuery(Context context, 
			Class<T> clazz, 
			Iterator<Entry<String, Object>> iterator,
			int depth) 
	throws NoSuchFieldException {
		
		JoinStatement selfJoin = new JoinStatement();
		selfJoin.left(getTableName(clazz), "self" + depth);
		
		SelectStatement subSelect = new SelectStatement();
		Entry<String, Object> entry = iterator.next();
		
		String key = entry.getKey();
		Object value = entry.getValue();
		
		String object = String.valueOf(value);
		
		if(value instanceof Model) {
			object = String.valueOf(((Model) value).getId());
		}
		
		List<String> fields = Arrays.asList(key.split("__"));
		
		if(fields.size() == 1) {
			Where where = new Where();
			where.and(key, object);
			
			subSelect.from(getTableName(clazz))
				  	 .where(where);
		} else {
			int left = depth + 1;
			int right = depth + 2;
			
			JoinStatement join = new JoinStatement();
			join.left(getTableName(clazz), "outer" + left)
				.right(buildJoin(context, clazz, fields, object, depth), "outer" + right)
				.on(PK, getTableName(clazz));
			
			subSelect.from(join)
				  	 .select(new String[] { "outer" + left + ".*"});
		}
		
		if(!iterator.hasNext()) {
			return subSelect;
		}
		
		selfJoin.right(subSelect, "self" + (depth + 1))
				.on(PK, PK);
		
		JoinStatement outerSelfJoin = new JoinStatement();
		outerSelfJoin.left(subSelect, "outerSelf" + depth)
					 .right(buildQuery(context, clazz, iterator, (depth + 2)), "outerSelf" + (depth + 1))
					 .on(PK, PK);
		
		SelectStatement select = new SelectStatement();
		select.from(outerSelfJoin)
			  .select(new String[] { "outerSelf" + depth + ".*"});
		
		return select;
		
	}
	
	protected static final <T extends Model> List<T> filter(Context context, 
			Class<T> clazz, 
			Map<String, Object> filter) {
		
		List<T> objects = new ArrayList<T>();
		
		Set<Entry<String, Object>> entries = filter.entrySet();
		Iterator<Entry<String, Object>> iterator = entries.iterator();
		
		SelectStatement select = new SelectStatement();
		try {
			select = buildQuery(context, clazz, iterator, 0);
		} catch (NoSuchFieldException e) {
			Log.e(TAG, "could not resolve fields into class.", e);
		}
		
		DatabaseAdapter adapter = new DatabaseAdapter(context);
		adapter.open();
		
		Cursor c = adapter.query(select.toString());
		
		try {
			Constructor<T> constructor = clazz.getConstructor(Context.class);
			
			while(c.moveToNext()) {
				T object = constructor.newInstance(context);
				
				createObject(object, clazz, c);
				objects.add(object);
			}
		} catch(Exception e) {
			// discard
		}
		
		return objects;
	}
	
	@SuppressWarnings("unchecked")
	private static final<T extends Model> void getFieldDefinitions(T object, 
			Class<?> clazz, 
			TableDefinition modelTable) 
	throws IllegalArgumentException, IllegalAccessException {
		
		if(clazz.isInstance(object) && !clazz.equals(Object.class)) {
			Field[] fields = clazz.getDeclaredFields();
			
			// TODO: only create fields from superclass, if superclass is
			// abstract. Otherwise create a pointer to superclass.
			
			for(int i = 0, length = fields.length; i < length; i++) {
				Field field = fields[i];
				field.setAccessible(true);
				
				String name = field.getName();

				try {
					Object o = field.get(object);
					
					if(o instanceof DataField) {
						DataField<?> fieldObject = (DataField<?>) o;
						modelTable.addField(name, fieldObject);
					}
					
					if(o instanceof ManyToManyField) {
						modelTable.addRelationalClass((Class<? extends Model>) clazz);
					}
				} catch(IllegalAccessException e) {
					// discard and move to the next field					
				}
			}
			
			getFieldDefinitions(object, clazz.getSuperclass(), modelTable);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static final <T extends Model> void getRelationDefinitions(T object, Class<?> clazz, List<TableDefinition> definitions) {
		if(clazz != null && clazz.isInstance(object) && !clazz.equals(Object.class)) {
			Field[] fields = clazz.getDeclaredFields();
			
			for(int i = 0, length = fields.length; i < length; i++) {
				Field field = fields[i];
				field.setAccessible(true);
				
				try {
					Object o = field.get(object);
					
					if(o instanceof ManyToManyField) {
						ManyToManyField<T, ?> m = (ManyToManyField<T, ?>) o;

						String leftHand = getTableName(clazz);
						String rightHand = getTableName(m.getTarget());
						
						TableDefinition definition = new TableDefinition(m.getRelationTableName());
						
						ForeignKeyField<T> leftLink = m.getLeftLinkDescriptor();
						ForeignKeyField<?> rightLink = m.getRightHandDescriptor();
						
						definition.addField(leftHand, leftLink);
						definition.addField(rightHand, rightLink);
						
						definitions.add(definition);
					}
				} catch(IllegalAccessException e) {
					// discard and continue
				}
			}
			
			getRelationDefinitions(object, clazz.getSuperclass(), definitions);
		}
	}
	
	private static final<T extends Model> List<TableDefinition> getRelationDefinitions(Context context, Class<T> clazz) {
		List<TableDefinition> definitions = new ArrayList<TableDefinition>();
		
		T object = null;
		
		try {
			Constructor<T> constructor = clazz.getConstructor(Context.class);
			object = constructor.newInstance(context);
		} catch(Exception e) {
			
		}
		
		getRelationDefinitions(object, clazz, definitions);
		
		return definitions;
	}
	
	protected static final<T extends Model> List<TableDefinition> getTableDefinitions(Class<T> clazz) {
		List<TableDefinition> definitions = new ArrayList<TableDefinition>();
		
		// TODO: only create table definition, if class is not abstract.
		try {
			Constructor<T> constructor = clazz.getConstructor(Context.class);
			T object = constructor.newInstance((Context) null);
			
			TableDefinition definition = new TableDefinition(getTableName(clazz));
			
			getFieldDefinitions(object, clazz, definition);
			
			definitions.add(definition);
			
			for(Class<? extends Model> c: definition.getRelationalClasses()) {
				definitions.addAll(getRelationDefinitions(null, c));
			}
			
			return definitions;
		} catch(Exception e) {
			Log.e("foo", "bar", e);
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static final <T extends Model, O extends Model> ForeignKeyField<O> getForeignKey(T object, Class<?> clazz, Class<O> target) 
	throws IllegalArgumentException, IllegalAccessException {
		
		ForeignKeyField<O> fk = null;
		
		if(clazz.isInstance(object) && !clazz.equals(Object.class)) {
			Field[] fields = clazz.getDeclaredFields();
			
			for(int i = 0, length = fields.length; i < length; i++) {
				Field field = fields[i];
				field.setAccessible(true);
				
				Object f = field.get(object);
				
				if(f instanceof ForeignKeyField) {
					ForeignKeyField<?> tmpFk = (ForeignKeyField<?>) f;
					Class<? extends Model> t = tmpFk.getTarget();
					
					if(t.equals(target)) {
						fk = (ForeignKeyField<O>) tmpFk;
						break;
					}
				}
			}
			
			if(fk == null) {
				fk = getForeignKey(object, clazz.getSuperclass(), target);
			}
		}
		
		return fk;
	}
	
	protected static final <T extends Model, O extends Model> void setBackLink(T target, 
			Class<T> targetClass,
			O origin, 
			Class<O> originClass) 
	throws NoSuchFieldException {
		
		ForeignKeyField<T> fk = null;
		
		try {
			fk = getForeignKey(origin, originClass, targetClass);
		} catch(IllegalAccessException e) {
			Log.e(TAG, "an exception was thrown trying to gather a foreign key field pointing to " 
					+ targetClass.getSimpleName() 
					+ " on an instance of class " 
					+ originClass.getSimpleName(), e);
		}
		
		if(fk != null) {
			fk.set(target);
		} else {
			throw new NoSuchFieldException("No field pointing to " 
					+ targetClass.getSimpleName() 
					+ " was found in class " 
					+ originClass.getSimpleName() 
					+"! Choices are: " 
					+ getEligableFields(originClass).toString());
		}
	}
	
	private static final <T extends Model, O extends Model> Field getForeignKeyField(Class<T> target, Class<?> clazz, O origin) 
	throws IllegalArgumentException, IllegalAccessException {
		
		Field fk= null;
		
		if(clazz.isInstance(origin) && !clazz.equals(Object.class)) {
			Field[] fields = clazz.getDeclaredFields();
			
			for(int i = 0, length = fields.length; i < length; i++) {
				Field field = fields[i];
				field.setAccessible(true);
				
				Object f = field.get(origin);
				
				if(f instanceof ForeignKeyField) {
					ForeignKeyField<?> tmp = (ForeignKeyField<?>) f;
					Class<? extends Model> t = tmp.getTarget();
					
					if(t.equals(target)) {
						fk = field;
						break;
					}
				}
			}
			
			if(fk == null) {
				fk = getForeignKeyField(target, clazz.getSuperclass(), origin);
			}
		}
		
		return fk;
	}
	
	protected static final <T extends Model, O extends Model> String getBackLinkFieldName(Context context, 
			Class<T> targetClass, 
			Class<O> originClass) {
		
		Field fk = null;
		
		try {
			Constructor<O> constructor = originClass.getConstructor(Context.class);
			O origin = constructor.newInstance(context);
			
			fk = getForeignKeyField(targetClass, originClass, origin);
		}  catch (Exception e) {
			Log.e(TAG, "an exception has been thrown trying to gather the foreign key field pointing to " 
					+ targetClass.getSimpleName() 
					+ " from origin class " 
					+ originClass.getSimpleName(), e);
		}
		
		if(fk != null) {
			return fk.getName();
		}
		
		return null;
	}
	
	protected PrimaryKeyField mId;
	
	protected Context mContext;
	
	public Model(Context context) {
		mContext = context;
		
		mId = new PrimaryKeyField(true);
	}
	
	public Model(Context context, boolean suppressAutoincrement) {
		mContext = context;
		
		mId = new PrimaryKeyField(!suppressAutoincrement);
	}
	
	private void putBooleanValue(Object field, String fieldName, ContentValues values) {
		if(field instanceof BooleanField) {
			BooleanField b = (BooleanField) field;
			values.put(fieldName, b.get(mContext));
		}
	}
	
	private void putIntegerValue(Object field, String fieldName, ContentValues values) {
		if(field instanceof IntegerField
				&& !(field instanceof PrimaryKeyField)) {
			
			IntegerField in = (IntegerField) field;
			values.put(fieldName, in.get(mContext));
		}
	}
	
	private void putStringValue(Object field, String fieldName, ContentValues values) {
		if(field instanceof CharField) {
			CharField c = (CharField) field;
			values.put(fieldName, c.get(mContext));
		}
	}
	
	private void putForeignKeyValue(Object field, String fieldName, ContentValues values) {
		if(field instanceof ForeignKeyField<?>) {
			ForeignKeyField<?> r = (ForeignKeyField<?>) field;
			
			if(r.isPersisted()) {
				values.put(fieldName, r.get(mContext).getId());
			} else {
				values.put(fieldName, 0);
			}
		}
	}
	
	private void collectData(ContentValues values, 
			Class<?> clazz) 
	throws IllegalArgumentException, IllegalAccessException {
		
		if(clazz.isInstance(this) && !clazz.equals(Object.class)) {
			Field[] fields = clazz.getDeclaredFields();
			
			for(int i = 0, length = fields.length; i < length; i++) {
				Field field = fields[i];
				field.setAccessible(true);
				
				Object o = field.get(this);
				String fieldName = field.getName();
				
				putBooleanValue(o, fieldName, values);
				putIntegerValue(o, fieldName, values);
				putStringValue(o, fieldName, values);
				putForeignKeyValue(o, fieldName, values);
			}
			
			collectData(values, clazz.getSuperclass());
		}
	}
	
	private <T extends Model> void saveRelationToDatabase(Class<?> clazz, 
			List<? extends Model> targets,
			ManyToManyField<T, ?> m) {
		
		DatabaseAdapter adapter = new DatabaseAdapter(mContext);
		
		for(Model target: targets) {
			/*
			 * Only save relation to the database if the
			 * target model has been persisted. 
			 */
			if(target.getId() != 0) {
				ContentValues values = new ContentValues();
				Where where = new Where();
				where.and(getTableName(clazz), getId())
					 .and(getTableName(m.getTarget()), target.getId());
				
				values.put(getTableName(clazz), getId());
				values.put(getTableName(m.getTarget()), target.getId());
				
				adapter.doInsertOrUpdate(m.getRelationTableName(), values, where);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T extends Model> void persistRelations(Class<?> clazz) 
	throws IllegalArgumentException, IllegalAccessException {
		
		if(clazz.isInstance(this) && !clazz.equals(Object.class)) {
			Field[] fields = clazz.getDeclaredFields();
			
			for(int i = 0, length = fields.length; i < length; i++) {
				Field field = fields[i];
				field.setAccessible(true);
				
				Object o = field.get(this);
				
				if(o instanceof ManyToManyField) {
					ManyToManyField<T, ?> m = (ManyToManyField<T, ?>) o;
					List<? extends Model> targets = m.get(mContext, (T) this);
					
					saveRelationToDatabase(clazz, targets, m);
				}
				
				if(o instanceof OneToManyField) {
					OneToManyField<T, ?> om = (OneToManyField<T, ?>) o;
					List<? extends Model> targets = om.get(mContext, (T) this);
					
					for(Model target: targets) {
						if(target.getId() != 0) {
							target.save(target.getId());
						}
					}
				}
			}
			
			persistRelations(clazz.getSuperclass());
		}
	}
	
	public boolean save(int id) {
		if(!mId.isAutoincrement()) {
			mId.set(id);
			
			ContentValues values = new ContentValues();
			values.put(PK, id);
			
			return save(id, this.getClass(), values);
		}
		
		return false;
	}

	private <T extends Model> boolean save(int id, Class<T> clazz, ContentValues values) {
		try {
			collectData(values, clazz);
		} catch(IllegalAccessException e) {
			Log.e(TAG, "exception thrown while gathering data from object", e);
		}
		
		Where where = new Where();
		where.and(PK, id);
		
		DatabaseAdapter adapter = new DatabaseAdapter(mContext);
		int result = adapter.doInsertOrUpdate(getTableName(clazz), values, where);

		if(result == -1) {
			mId.set(0);
			return false;
		} 
		
		if(mId.get(mContext) == 0) {
			mId.set(result);
		}
		
		try {
			persistRelations(clazz);
		} catch (IllegalAccessException e) {
			Log.e(TAG, "an exception has been thrown trying to save the relations for " 
					+ clazz.getSimpleName(), e);
			
			return false;
		}
		
		return true;
		
	}
	
	public boolean save() {
		if(mId.isAutoincrement()) {
			int id = mId.get(mContext);
			
			ContentValues values = new ContentValues();
			return save(id, this.getClass(), values);
		}
		
		return false;
	}
	
	public int getId() {
		return mId.get(mContext);
	}
	
	private class PrimaryKeyField extends IntegerField {
		
		private boolean mAutoIncrement;
		
		public PrimaryKeyField(boolean autoincrement) {
			super();
			
			mAutoIncrement = autoincrement;
		}
		
		@Override
		public String getDefinition(String fieldName) {
			String definition = super.getDefinition(fieldName)
				+ " PRIMARY KEY";
			
			if(mAutoIncrement) {
				definition += " autoincrement";
			}
			
			return definition;
		}
		
		public boolean isAutoincrement() {
			return mAutoIncrement;
		}
		
	}
}
