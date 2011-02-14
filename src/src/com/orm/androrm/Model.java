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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.AvoidXfermode.Mode;
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
	 * <br /><br />
	 * Always keep consistent with the {@link PrimaryKeyField}
	 * of this class.
	 */
	public static final String PK = "mId";
	
	/**
	 * This is the name used, when selecting COUNT values
	 * from the database.
	 */
	public static final String COUNT = "androrm_item_count";
	
	/**
	 * Assigns a value gathered from the database to the
	 * instance <code>object</code> of type T. Due to the nature
	 * of this ORM only fields applicable for serialization
	 * will be considered.
	 * 
	 * @param <T>		{@link Type} of the object.
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
	
	/**
	 * Creates an instance of the given class, and populates the fields.
	 * 
	 * @param <T>	{@link Type} of the instance.
	 * @param clazz	{@link Class} of the instance.
	 * @param c		{@link Cursor} used to retrieve data.
	 * @return	Instance of type <code>T</code>.
	 */
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
	
	/**
	 * Populates all fields of an instance with the values gathered form the database.
	 * 
	 * @param <T>		{@link Type} of the instance.
	 * @param instance	Object instance, that will be equipped with data.
	 * @param clazz		{@link Class} of the instance.
	 * @param c			{@link Cursor}, that is used to navigate over data.
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
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
	
	/**
	 * This method is used, to find {@link ForeignKeyField} on a class, that point
	 * to a certain target class. This method is used by the {@link OneToManyField}
	 * in order, to find all fields, that a linking the class, it is defined on.
	 * 
	 * @param <O>			Type of the origin class.
	 * @param <T>			Type of the target class.
	 * @param originClass	{@link Class} on which the {@link ForeignKeyField} should
	 * 						be defined on.
	 * @param targetClass	{@link Class} the {@link ForeignKeyField} should point to.
	 * @return
	 * @throws NoSuchFieldException 
	 */
	protected static final <O extends Model, T extends Model> String getBackLinkFieldName(
			
			Class<O> originClass,
			Class<T> targetClass
			
	) throws NoSuchFieldException {
		
		Field fk = getForeignKeyField(targetClass, originClass, getInstace(originClass));
		
		return fk.getName();
	}
	
	/**
	 * Find all fields on a class (and inherited fields), that
	 * are eligible for a database lookup.
	 * 
	 * @param <T>
	 * @param clazz		{@link Class} of the instance.
	 * @param instance	Object instance.
	 * @return {@link List} of field names.
	 */
	protected static final <T extends Model> List<String> getEligibleFields(
			
			Class<T> 	clazz, 
			T 			instance
			
	) {
		
		List<String> eligableFields = new ArrayList<String>();
		
		if(clazz != null) {
			for(Field field: DatabaseBuilder.getFields(clazz, instance)) {
				eligableFields.add(field.getName());
			}
			
			eligableFields.addAll(getEligibleFields(getSuperclass(clazz), instance));
		}
		
		return eligableFields;
	}
	
	/**
	 * Searches for a field on a given object instance and all of it's 
	 * superclasses.
	 * 
	 * @param <T>		Instance type.
	 * @param clazz		Class of instance.
	 * @param instance	Object instance.
	 * @param fieldName	Name of the field, that you are looking for.
	 * @return	{@link Field} instance.
	 * @throws NoSuchFieldException
	 */
	protected static final <T extends Model> Field getField(
			
			Class<T> 	clazz, 
			T 			instance, 
			String 		fieldName
			
	) throws NoSuchFieldException {
		
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
		}
		
		if(field == null) {
			throw new NoSuchFieldException(fieldName, getEligibleFields(clazz, instance));
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
	
	/**
	 * Searches for a {@link ForeignKeyField} on the origin class
	 * and its superclasses.
	 * 
	 * @param <T>			{@link Type} of the target.
	 * @param <O>			{@link Type} of the origin.
	 * @param targetClass	{@link Class} of the target.
	 * @param originClass	{@link Class} of the origin.
	 * @param origin		Instance of the origin class.
	 * @return {@link Field} instance of the {@link ForeignKeyField}.
	 * @throws NoSuchFieldException
	 */
	private static final <T extends Model, O extends Model> Field getForeignKeyField(
			
			Class<T> 	targetClass, 
			Class<O> 	originClass, 
			O 			origin
			
	) throws NoSuchFieldException {
		
		Field fk = null;
		
		if(originClass != null && originClass.isInstance(origin)) {
			for(Field field: DatabaseBuilder.getFields(originClass, origin)) {
				Object f = null;
				
				try {
					f = field.get(origin);
					
					if(f instanceof ForeignKeyField) {
						ForeignKeyField<?> tmp = (ForeignKeyField<?>) f;
						Class<? extends Model> t = tmp.getTarget();
						
						if(t.equals(targetClass)) {
							fk = field;
							break;
						}
					}
				} catch (IllegalAccessException e) {
					Log.e(TAG, "an exception has been thrown trying to gather the foreign key field pointing to " 
							+ targetClass.getSimpleName() 
							+ " from origin class " 
							+ originClass.getSimpleName(), e);
				}
			}
			
			if(fk == null) {
				fk = getForeignKeyField(targetClass, getSuperclass(originClass), origin);
			}
		}
		
		if(fk == null) {
			throw new NoSuchFieldException("Could not find field on " 
					+ DatabaseBuilder.getTableName(originClass) 
					+ " that is defined as a ForeignKey and that points to " 
					+ DatabaseBuilder.getTableName(targetClass));
		}
		
		return fk;
	}
	
	/**
	 * Create an instance of a given class. This method expects
	 * a <b>zero-argument</b> constructor on the class.
	 * 
	 * @param <T>	{@link Type} of the instance.
	 * @param clazz	{@link Class} of the instance.
	 * @return Instance of type <code>T</code> or <code>null</code>.
	 */
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
	
	/**
	 * Use this method to gather the superclass of a {@link Model} class.
	 * This method will only return superclasses, until the {@link Mode}
	 * class itsel is reached. This way you don't have to check, if the
	 * {@link Object} class has been reached.
	 * 
	 * @param <T>	{@link Type} of the subclass.
	 * @param <U>	{@link Type} of the superclass.
	 * @param clazz	Subclass.
	 * @return Superclass of <code>clazz</code> or <code>null</code>.
	 */
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
					+ getEligibleFields(originClass, origin));
		}
	}

	/**
	 * ID field, that will be automatically assigned to each model class.
	 * If you want to get the name of this field use {@link Model#PK}.
	 */
	protected PrimaryKeyField mId;
	
	public Model() {
		mId = new PrimaryKeyField();
	}
	
	public Model(boolean suppressAutoincrement) {
		mId = new PrimaryKeyField(!suppressAutoincrement);
	}
	
	/**
	 * Queries each field for its current value. 
	 * 
	 * @param <T>		{@link Type} of the instance.
	 * @param context	{@link Context} the application runs in.
	 * @param values	{@link ContentValues} that the data is put into.
	 * @param clazz		{@link Class} of the instance.
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
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
				
				return resetFields(context);
			}
		}
		
		return false;
	}
	
	/**
	 * Called when an instance is being deleted. This will reset
	 * each field to it's default value and also remove relations. 
	 * 
	 * @param <T>		{@link Type} of the instance.
	 * @param context	{@link Context} this application runs in.
	 * @return <code>true</code> if all fields could be reset.
	 */
	private <T extends Model> boolean resetFields(Context context) {
		List<Field> fields = DatabaseBuilder.getFields(getClass(), this);
		
		try {
			for(Field field : fields) {
				Object o = field.get(this);
				
				if(o instanceof AndrormField) {
					AndrormField f = (AndrormField) o;
					f.reset(context, this);
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
			
			return getClass().equals(m.getClass())
					&& getId() == m.getId();
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
	
	/**
	 * If you do not suppress automatic primary key generation
	 * (call <code>super()</code> in your constructor without 
	 * any arguments), call this save function to store your
	 * object in the database.
	 * 
	 * @param context	{@link Context} your application runs in.
	 * @return <code>true</code> on success.
	 */
	public boolean save(Context context) {
		if(mId.isAutoincrement() || getId() != 0) {
			return save(context, getId(), new ContentValues());
		}
		
		return false;
	}
	
	/**
	 * If you suppress automatic primary key creation, use
	 * this function to store your instance in the database, 
	 * by handing in a unique id.
	 * 
	 * @param context	{@link Context} your application runs in.
	 * @param id		ID under which the object will be stored.
	 * @return <code>true</code> on success.
	 */
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
		} catch (IllegalAccessException e) {
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
	
	/**
	 * To perform any kind of query, first call this function, to 
	 * obtain a set-up {@link QuerySet} instance. 
	 * <br /><br />
	 * Subclasses of {@link Model} should implement their own 
	 * <code>objects</code> function, calling this one and handing in
	 * the class param.
	 * 
	 * @param <T>		{@link Type} of the instance calling.
	 * @param context	{@link Context} the application runs in.
	 * @param clazz		{@link Class} requesting the {@link QuerySet}.
	 * @return	{@link QuerySet} instance for the given class and type.
	 */
	public static <T extends Model> QuerySet<T> objects(
			
			Context 	context, 
			Class<T> 	clazz
			
	) {
		return new QuerySet<T>(context, clazz);
	}
}
