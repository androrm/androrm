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
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;


/**
 * @author Philipp Giese
 *
 */
public abstract class Model {
	
	private static final String TAG = "ANDRORM:MODEL";
	
	public static final String PK = "mId";
	
	private static final String COUNT = "count";
	
	public static final String getTableName(Class<?> clazz) {
		return clazz.getSimpleName().toLowerCase();
	}
	
	/**
	 * Assigns a value gathered from the database to the
	 * instance <code>object</b> of type T. Due to the nature
	 * of this ORM only fields applicable for serialization
	 * will be considered.
	 * 
	 * @param <T>		Type of the object.
	 * @param field		Field of the object, that a value shall be assigned to.
	 * @param object	Object instance of type <T>.
	 * @param c			Database {@link Cursor}
	 * 
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private static final <T extends Model> void assignFieldValue(Field field, 
			T object,
			Cursor c) 
	throws IllegalArgumentException, IllegalAccessException {
		
		Object o = field.get(object);
		
		if(o instanceof DataField) {
			DataField<?> f = (DataField<?>) o;
			String fieldName = field.getName();
			int columnIndex = c.getColumnIndexOrThrow(fieldName);
		
			f.set(c, columnIndex);
		}
	}
	
	protected static final <T extends Model> List<Field> getFields(Class<T> clazz, T instance) {
		Field[] declaredFields = clazz.getDeclaredFields();
		List<Field> fields = new ArrayList<Field>();
		
		try {
			for(int i = 0, length = declaredFields.length; i < length; i++) {
				Field field = declaredFields[i];
				field.setAccessible(true);
				Object f = field.get(instance);
				
				if(QueryBuilder.isDatabaseField(f)) {
					fields.add(field);
				}
			}
		} catch (IllegalAccessException e) {
			Log.e(TAG, "exception thrown while trying to gain access to fields of class " 
					+ clazz.getSimpleName(), e);
		}
		
		return fields;
	}
	
	@SuppressWarnings("unchecked")
	protected static final <T extends Model, U extends Model> Class<U> getSuperclass(Class<T> clazz) {
		Class<?> parent = clazz.getSuperclass();
		Class<U> superclass = null;
		
		if(!parent.equals(Object.class)) {
			superclass = (Class<U>) parent;
		}
		
		return superclass;
	}
	
	private static final <T extends Model> void fillUpData(T instance, 
			Class<T> clazz, 
			Cursor c) 
	throws IllegalArgumentException, IllegalAccessException {
		
		if(clazz != null && clazz.isInstance(instance)) {
			
			for(Field field: getFields(clazz, instance)) {
				assignFieldValue(field, instance, c);
			}
			
			fillUpData(instance, getSuperclass(clazz), c);
		}
	}
	
	protected static final <T extends Model> T createObject(Class<T> clazz,
			Cursor c) {
		
		T object = null;
		
		try {
			Constructor<T> constructor = clazz.getConstructor();
			object = constructor.newInstance();
			
			fillUpData(object, clazz, c);
		} catch(Exception e) {
			Log.e(TAG, "exception thrown while gathering representation for object of class " 
					+ clazz.getSimpleName(), e);
		}
		
		return object;
	}
	
	public static final <T extends Model> T get(Context context, Class<T> clazz, int id) {
		Where where = new Where();
		where.and(PK, id);
		
		DatabaseAdapter adapter = new DatabaseAdapter(context);
		adapter.open();
		
		SelectStatement select = new SelectStatement();
		select.from(getTableName(clazz))
			  .where(where);
		
		Cursor c = adapter.query(select);
		
		T object = null;
		
		if(c.moveToNext()) {
			object = createObject(clazz, c);
		}
		
		c.close();
		adapter.close();
		return object;
	}
	
	protected static final <T extends Model> List<String> getEligableFields(Class<T> clazz, T instance) {
		List<String> eligableFields = new ArrayList<String>();
		
		if(clazz != null) {
			for(Field field: getFields(clazz, instance)) {
				eligableFields.add(field.getName());
			}
			
			eligableFields.addAll(getEligableFields(getSuperclass(clazz), instance));
		}
		
		return eligableFields;
	}
	
	private static final <T extends Model> List<T> createObjects(Context context, Class<T> clazz, SelectStatement select) {
		List<T> objects = new ArrayList<T>();
		
		DatabaseAdapter adapter = new DatabaseAdapter(context);
		adapter.open();
		
		Cursor c = adapter.query(select);
		
		while(c.moveToNext()) {
			T object = createObject(clazz, c);
			
			if(object != null) {
				objects.add(object);
			}
		}
		
		c.close();
		adapter.close();
		return objects;
	}
	
	protected static final <T extends Model> List<T> filter(Context context, 
			Class<T> clazz, 
			FilterSet filter,
			Limit limit) {
		
		SelectStatement select = new SelectStatement();
		
		try {
			select = QueryBuilder.buildQuery(clazz, filter.getFilters(), 0);
			select.limit(limit);
		} catch (NoSuchFieldException e) {
			Log.e(TAG, "could not resolve fields into class.", e);
		}
		
		return createObjects(context, clazz, select);
	}
	
	protected static final <T extends Model> List<T> filter(Context context,
			Class<T> clazz,
			FilterSet filter) {
		
		return filter(context, clazz, filter, null);
	}
	
	private static final<T extends Model> void getFieldDefinitions(T instance, 
			Class<T> clazz, 
			TableDefinition modelTable) 
	throws IllegalArgumentException, IllegalAccessException {
		
		if(clazz != null && clazz.isInstance(instance)) {
			// TODO: only create fields from superclass, if superclass is
			// abstract. Otherwise create a pointer to superclass.
			
			for(Field field: getFields(clazz, instance)) {
				String name = field.getName();

				try {
					Object o = field.get(instance);
					
					if(o instanceof DataField) {
						DataField<?> fieldObject = (DataField<?>) o;
						modelTable.addField(name, fieldObject);
					}
					
					if(o instanceof ManyToManyField) {
						modelTable.addRelationalClass(clazz);
					}
				} catch(IllegalAccessException e) {
					Log.e(TAG, "could not create field definitions for class " 
							+ clazz.getSimpleName(), e);	
				}
			}
			
			getFieldDefinitions(instance, getSuperclass(clazz), modelTable);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static final <T extends Model> void getRelationDefinitions(T instance, 
			Class<T> clazz, 
			List<TableDefinition> definitions) {
		
		if(clazz != null && clazz.isInstance(instance) ) {
			for(Field field: getFields(clazz, instance)) {
				try {
					Object o = field.get(instance);
					
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
					Log.e(TAG, "could not gather relation definitions for class " 
							+ clazz.getSimpleName(), e);
				}
			}
			
			getRelationDefinitions(instance, getSuperclass(clazz), definitions);
		}
	}
	
	private static final<T extends Model> List<TableDefinition> getRelationDefinitions(Class<T> clazz) {
		List<TableDefinition> definitions = new ArrayList<TableDefinition>();
		
		T object = null;
		
		try {
			Constructor<T> constructor = clazz.getConstructor();
			object = constructor.newInstance();
			
			getRelationDefinitions(object, clazz, definitions);
		} catch(Exception e) {
			Log.e(TAG, "could not create instance of class " 
					+ clazz.getSimpleName(), e);
		}
		
		
		
		return definitions;
	}
	
	protected static final<T extends Model> List<TableDefinition> getTableDefinitions(Class<T> clazz) {
		List<TableDefinition> definitions = new ArrayList<TableDefinition>();
		
		// TODO: only create table definition, if class is not abstract.
		try {
			Constructor<T> constructor = clazz.getConstructor();
			T object = constructor.newInstance();
			
			TableDefinition definition = new TableDefinition(getTableName(clazz));
			
			getFieldDefinitions(object, clazz, definition);
			
			definitions.add(definition);
			
			for(Class<? extends Model> c: definition.getRelationalClasses()) {
				definitions.addAll(getRelationDefinitions(c));
			}
			
			return definitions;
		} catch(Exception e) {
			Log.e(TAG, "an exception has been thrown while gathering the database structure information.", e);
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static final <T extends Model, O extends Model> ForeignKeyField<T> getForeignKey(O origin, 
			Class<O> originClass, 
			Class<T> target) 
	throws IllegalArgumentException, IllegalAccessException {
		
		if(originClass != null && originClass.isInstance(origin)) {
			Field fkField = getForeignKeyField(target, originClass, origin);
			
			if(fkField != null) {
				return (ForeignKeyField<T>) fkField.get(origin);
			}
		}
		
		return null;
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
					+ getEligableFields(originClass, origin).toString());
		}
	}
	
	private static final <T extends Model, O extends Model> Field getForeignKeyField(Class<T> target, 
			Class<O> originClass, 
			O origin) 
	throws IllegalArgumentException, IllegalAccessException {
		
		Field fk = null;
		
		if(originClass != null && originClass.isInstance(origin)) {
			for(Field field: getFields(originClass, origin)) {
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
				fk = getForeignKeyField(target, getSuperclass(originClass), origin);
			}
		}
		
		return fk;
	}
	
	protected static final <O extends Model, T extends Model> String getBackLinkFieldName(Class<O> originClass,
			Class<T> targetClass) {
		
		Field fk = null;
		
		try {
			Constructor<O> constructor = originClass.getConstructor();
			O origin = constructor.newInstance();
			
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
	
	public static final <T extends Model> int count(Context context, 
			Class<T> clazz, 
			FilterSet filter) {
		
		DatabaseAdapter adapter = new DatabaseAdapter(context);
		adapter.open();
		
		Cursor c = null;
		
		try {
			SelectStatement select = QueryBuilder.buildQuery(clazz, filter.getFilters(), 0);
			select.count();
			
			c = adapter.query(select);
			
			int count = 0;
			if(c.moveToNext()) {
				count = c.getInt(c.getColumnIndexOrThrow(COUNT));
			}
			
			return count;
		} catch (NoSuchFieldException e) {
			Log.e(TAG, "exception thrown trying to count objects of class " 
					+ clazz.getSimpleName() 
					+ ". Check your filters!", e);
		} finally {
			if(c != null) {
				c.close();
			}
			
			adapter.close();
		}
		
		return 0;
	}
	
	public static final <T extends Model> int count(Context context, 
			Class<T> clazz) {
		
		SelectStatement select = new SelectStatement();
		select.count()
			  .from(getTableName(clazz));
		
		DatabaseAdapter adapter = new DatabaseAdapter(context);
		adapter.open();
		
		Cursor c = adapter.query(select);
		
		int count = 0;
		if(c.moveToNext()) {
			count = c.getInt(c.getColumnIndexOrThrow(COUNT));
		}
		
		c.close();
		adapter.close();
		return count;
	}
	
	public static final <T extends Model> List<T> all(Context context, Class<T> clazz, Limit limit) {
		SelectStatement select = new SelectStatement();
		select.from(getTableName(clazz))
			  .limit(limit);
		
		return createObjects(context, clazz, select);
	}
	
	protected PrimaryKeyField mId;

	public Model() {
		mId = new PrimaryKeyField(true);
	}
	
	public Model(boolean suppressAutoincrement) {
		mId = new PrimaryKeyField(!suppressAutoincrement);
	}
	
	private boolean handledByPrimaryKey(Object field) {
		if(field instanceof PrimaryKeyField) {
			PrimaryKeyField pk = (PrimaryKeyField) field;
			if(pk.isAutoincrement()) {
				return true;
			}
		}
		
		return false;
	}
	
	private void putValue(Object field, String fieldName, ContentValues values) {
		if(field instanceof DataField
			&& !handledByPrimaryKey(field)) {
			
			DataField<?> f = (DataField<?>) field;
			f.putData(fieldName, values);
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T extends Model> void collectData(Context context, 
			ContentValues values, 
			Class<T> clazz) 
	throws IllegalArgumentException, IllegalAccessException {
		
		if(clazz != null && clazz.isInstance(this)) {
			for(Field field: getFields(clazz, (T) this)) {
				Object o = field.get(this);
				String fieldName = field.getName();
				
				putValue(o, fieldName, values);
			}
			
			collectData(context, values, getSuperclass(clazz));
		}
	}
	
	private <T extends Model> void saveM2MToDatabase(Context context, 
			Class<T> clazz, 
			List<? extends Model> targets,
			ManyToManyField<T, ?> m) {
		
		DatabaseAdapter adapter = new DatabaseAdapter(context);
		
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
	private <T extends Model, O extends Model> void persistRelations(Context context, 
			Class<T> clazz) 
	throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
		
		if(clazz != null && clazz.isInstance(this)) {
			
			for(Field field: getFields(clazz, (T) this)) {
				Object o = field.get(this);
				
				if(o instanceof ManyToManyField) {
					ManyToManyField<T, ?> m = (ManyToManyField<T, ?>) o;
					List<? extends Model> targets = m.get(context, (T) this);
					
					saveM2MToDatabase(context, clazz, targets, m);
				}
				
				if(o instanceof OneToManyField) {
					OneToManyField<T, ?> om = (OneToManyField<T, ?>) o;
					List<? extends Model> targets = om.get(context, (T) this);
					
					for(Model target: targets) {
						if(target.getId() != 0) {
							setBackLink((T) this, (Class<T>) getClass(), (O) target, (Class<O>) target.getClass());
							target.save(context);
						}
					}
				}
			}
			
			persistRelations(context, getSuperclass(clazz));
		}
	}
	
	public boolean save(Context context, int id) {
		if(!mId.isAutoincrement()) {
			mId.set(id);
			
			ContentValues values = new ContentValues();
			values.put(PK, id);
			
			return save(context, id, values);
		}
		
		return false;
	}

	private <T extends Model> boolean save(Context context, 
			int id, 
			ContentValues values) {
		
		try {
			collectData(context, values, getClass());
		} catch(IllegalAccessException e) {
			Log.e(TAG, "exception thrown while gathering data from object", e);
		}
		
		Where where = new Where();
		where.and(PK, id);
		
		DatabaseAdapter adapter = new DatabaseAdapter(context);
		int rowID = adapter.doInsertOrUpdate(getTableName(getClass()), values, where);

		if(rowID == -1) {
			mId.set(0);
			return false;
		} 
		
		if(getId() == 0) {
			mId.set(rowID);
		}
		
		try {
			persistRelations(context, getClass());
		} catch (Exception e) {
			Log.e(TAG, "an exception has been thrown trying to save the relations for " 
					+ getClass().getSimpleName(), e);
			
			return false;
		}
		
		return true;
		
	}
	
	public boolean save(Context context) {
		if(mId.isAutoincrement() || getId() != 0) {
			return save(context, getId(), new ContentValues());
		}
		
		return false;
	}
	
	public boolean delete(Context context) {
		if(getId() != 0) {
			Where where = new Where();
			where.and(PK, getId());
			
			DatabaseAdapter adapter = new DatabaseAdapter(context);
			int affectedRows = adapter.delete(getTableName(getClass()), where);
			
			return affectedRows != 0;
		}
		
		return false;
	}
	
	public int getId() {
		return mId.get();
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
	
	@Override
	public int hashCode() {
		return getId() + getClass().getSimpleName().hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Model) {
			Model m = (Model) o;
			
			if(getClass().equals(m.getClass())
					&& getId() == m.getId()) {
				
				return true;
			}
		}
		
		return false;
	}
}
