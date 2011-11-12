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
 * This is the superclass of all models, that can be stored/ read
 * to/ from the database automatically. 
 * 
 * @author Philipp Giese
 */
public abstract class Model {
	
	private class PrimaryKeyField extends IntegerField {
		
		private boolean mAutoIncrement;
		
		public PrimaryKeyField() {
			super();
			
			mAutoIncrement = true;
		}
		
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
	
	private static final String TAG = "ANDRORM:MODEL";
	
	/**
	 * Name used for the primary key field, that is
	 * automatically assigned to each model. 
	 */
	public static final String PK = "mId";
	
	public static final String COUNT = "item_count";
	
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
	private static final <T extends Model> void assignFieldValue(
			
			Field 	field, 
			T 		object,
			Cursor 	c
			
	) throws IllegalArgumentException, IllegalAccessException {
		
		Object o = field.get(object);
		
		if(o instanceof DataField) {
			DataField<?> f = (DataField<?>) o;
		
			f.set(c, field.getName());
		}
	}
	
	protected static final <T extends Model> T createObject(
			
			Class<T> clazz,
			Cursor	 c
			
	) {
		
		T object = getInstace(clazz);
		
		try {
			fillUpData(object, clazz, c);
		} catch(IllegalAccessException e) {
			Log.e(TAG, "exception thrown while filling instance of " 
					+ clazz.getSimpleName()
					+ " with data.", e);
		}
		
		return object;
	}
	
	private static final <T extends Model> void fillUpData(
			
			T 			instance, 
			Class<T> 	clazz, 
			Cursor 		c
			
	) throws IllegalArgumentException, IllegalAccessException {
		
		if(clazz != null && clazz.isInstance(instance)) {
			
			for(Field field: DatabaseBuilder.getFields(clazz, instance)) {
				assignFieldValue(field, instance, c);
			}
			
			fillUpData(instance, getSuperclass(clazz), c);
		}
	}
	
	protected static final <O extends Model, T extends Model> String getBackLinkFieldName(
			
			Class<O> originClass,
			Class<T> targetClass
			
	) {
		
		Field fk = null;
		
		try {
			fk = getForeignKeyField(targetClass, originClass, getInstace(originClass));
		}  catch (IllegalAccessException e) {
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
	
	private static final <T extends Model> List<String> getEligableFields(
			
			Class<? extends Model> 	clazz, 
			T 						instance
			
	) {
		
		List<String> eligableFields = new ArrayList<String>();
		
		if(clazz != null) {
			for(Field field: DatabaseBuilder.getFields(clazz, instance)) {
				eligableFields.add(field.getName());
			}
			
			eligableFields.addAll(getEligableFields(getSuperclass(clazz), instance));
		}
		
		return eligableFields;
	}
	
	protected static final <T extends Model> Field getField(
			
			Class<T> 	clazz, 
			T 			instance, 
			String 		fieldName
			
	) {
		
		Field field = null;
		
		if(clazz != null) {
			for(Field f: DatabaseBuilder.getFields(clazz, instance)) {
				if(f.getName().equals(fieldName)) {
					field = f;
					break;
				}
			}
			
			if(field == null) {
				field = getField(getSuperclass(clazz), instance, fieldName);
			}
			
			if(field == null) {
				throw new NoSuchFieldException("No field named " 
						+ fieldName 
						+ " was found in class " 
						+ instance.getClass().getSimpleName() 
						+"! Choices are: " 
						+ getEligableFields(instance.getClass(), instance).toString());
			}
		}
		
		return field;
	}
	
	@SuppressWarnings("unchecked")
	private static final <T extends Model, O extends Model> ForeignKeyField<T> getForeignKey(
			
			O 			origin, 
			Class<O> 	originClass, 
			Class<T> 	target
			
	) throws IllegalArgumentException, IllegalAccessException {
		
		if(originClass != null && originClass.isInstance(origin)) {
			Field fkField = getForeignKeyField(target, originClass, origin);
			
			if(fkField != null) {
				return (ForeignKeyField<T>) fkField.get(origin);
			}
		}
		
		return null;
	}
	
	private static final <T extends Model, O extends Model> Field getForeignKeyField(
			
			Class<T> 	target, 
			Class<O> 	originClass, 
			O 			origin
			
	) throws IllegalArgumentException, IllegalAccessException {
		
		Field fk = null;
		
		if(originClass != null && originClass.isInstance(origin)) {
			for(Field field: DatabaseBuilder.getFields(originClass, origin)) {
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
	
	protected static final <T extends Model> T getInstace(Class<T> clazz) {
		T instance = null;
		
		try {
			Constructor<T> constructor = clazz.getConstructor();
			instance = constructor.newInstance();
		} catch(Exception e) {
			Log.e(TAG, "exception thrown while trying to create representation of " 
					+ clazz.getSimpleName(), e);
		}
		
		return instance;
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
	
	private static final <T extends Model, O extends Model> void setBackLink(
			
			T 			target, 
			Class<T> 	targetClass,
			O 			origin, 
			Class<O> 	originClass
			
	) throws NoSuchFieldException {
		
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

	protected PrimaryKeyField mId;
	
	public Model() {
		mId = new PrimaryKeyField();
	}
	
	public Model(boolean suppressAutoincrement) {
		mId = new PrimaryKeyField(!suppressAutoincrement);
	}
	
	private <T extends Model> void collectData(
			
			Context 		context, 
			ContentValues 	values, 
			Class<T> 		clazz
			
	) throws IllegalArgumentException, IllegalAccessException {
		
		if(clazz != null && clazz.isInstance(this)) {
			for(Field field: DatabaseBuilder.getFields(clazz, this)) {
				Object o = field.get(this);
				String fieldName = field.getName();
				
				putValue(o, fieldName, values);
			}
			
			collectData(context, values, getSuperclass(clazz));
		}
	}
	
	public <T extends Model> boolean delete(Context context) {
		if(getId() != 0) {
			Where where = new Where();
			where.and(PK, getId());
			
			DatabaseAdapter adapter = new DatabaseAdapter(context);
			int affectedRows = adapter.delete(DatabaseBuilder.getTableName(getClass()), where);
			
			if(affectedRows != 0) {
				mId.set(0);
				
				return resetFields();
			}
		}
		
		return false;
	}
	
	private <T extends Model> boolean resetFields() {
		List<Field> fields = DatabaseBuilder.getFields(getClass(), this);
		
		try {
			for(Field field : fields) {
				Object o = field.get(this);
				
				if(o instanceof AndrormField) {
					AndrormField f = (AndrormField) o;
					f.reset();
				}
			}
			
			return true;
		} catch(IllegalAccessException e) {
			return false;
		}
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
	
	public int getId() {
		return mId.get();
	}
	
	private boolean handledByPrimaryKey(Object field) {
		if(field instanceof PrimaryKeyField) {
			PrimaryKeyField pk = (PrimaryKeyField) field;
			return pk.isAutoincrement();
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return getId() + getClass().getSimpleName().hashCode();
	}

	private <T extends Model, O extends Model> void persistRelations(
			
			Context	 context, 
			Class<T> clazz
			
	) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
		
		if(clazz != null && clazz.isInstance(this)) {
			
			for(Field field: DatabaseBuilder.getFields(clazz, this)) {
				Object o = field.get(this);
				
				if(o instanceof ManyToManyField) {
					saveM2MToDatabase(context, clazz, o);
				}
				
				if(o instanceof OneToManyField) {
					saveO2MToDatabase(context, o);
				}
			}
			
			persistRelations(context, getSuperclass(clazz));
		}
	}
	
	private void putValue(
			
			Object 			field, 
			String 			fieldName, 
			ContentValues 	values
			
	) {
		
		if(field instanceof DataField
			&& !handledByPrimaryKey(field)) {
			
			DataField<?> f = (DataField<?>) field;
			f.putData(fieldName, values);
		}
	}
	
	public boolean save(Context context) {
		if(mId.isAutoincrement() || getId() != 0) {
			return save(context, getId(), new ContentValues());
		}
		
		return false;
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
	
	private <T extends Model> boolean save(
			
			Context 		context, 
			int 			id, 
			ContentValues 	values
			
	) {
		
		try {
			collectData(context, values, getClass());
		} catch(IllegalAccessException e) {
			Log.e(TAG, "exception thrown while gathering data from object", e);
		}
		
		Where where = new Where();
		where.and(PK, id);
		
		DatabaseAdapter adapter = new DatabaseAdapter(context);
		int rowID = adapter.doInsertOrUpdate(DatabaseBuilder.getTableName(getClass()), values, where);

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
	
	@SuppressWarnings("unchecked")
	private <T extends Model> void saveM2MToDatabase(
			
			Context 	context, 
			Class<T> 	clazz, 
			Object 		field
			
	) {
		
		ManyToManyField<T, ?> m = (ManyToManyField<T, ?>) field;
		List<? extends Model> targets = m.getCachedValues();
		
		DatabaseAdapter adapter = new DatabaseAdapter(context);
		
		for(Model target: targets) {
			/*
			 * Only save relation to the database if the
			 * target model has been persisted. 
			 */
			if(target.getId() != 0) {
				ContentValues values = new ContentValues();
				Where where = new Where();
				where.and(DatabaseBuilder.getTableName(clazz), getId())
					 .and(DatabaseBuilder.getTableName(m.getTarget()), target.getId());
				
				values.put(DatabaseBuilder.getTableName(clazz), getId());
				values.put(DatabaseBuilder.getTableName(m.getTarget()), target.getId());
				
				adapter.doInsertOrUpdate(m.getRelationTableName(), values, where);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private <O extends Model, T extends Model> void saveO2MToDatabase(
			
			Context context, 
			Object 	field
			
	) throws NoSuchFieldException {
		
		OneToManyField<T, ?> om = (OneToManyField<T, ?>) field;
		List<? extends Model> targets = om.getCachedValues();
		
		for(Model target: targets) {
			/*
			 * Only save the target, if it has already been saved once to the database.
			 * Otherwise we could save objects, that shouldn't be saved. 
			 */
			if(target.getId() != 0) {
				setBackLink((T) this, (Class<T>) getClass(), (O) target, (Class<O>) target.getClass());
				target.save(context);
			}
		}
	}
	
	public static <T extends Model> QuerySet<T> objects(
			
			Context 	context, 
			Class<T> 	clazz
			
	) {
		return new QuerySet<T>(context, clazz);
	}
}
