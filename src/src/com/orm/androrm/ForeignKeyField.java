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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * The foreign key field can be used to build a relation
 * between two models. One model can create a link to 
 * another model by using a foreign key. The type parameter
 * is used to determine the linked model type. 
 * 
 * @author Philipp Giese
 *
 * @param <T>	Type of the linked model. 
 */
public class ForeignKeyField<T extends Model> extends DataField<T> implements Relation<T> {

	private Class<T> mTarget;
	private int mReference;
	private boolean mOnDeleteCascade;
	
	/**
	 * Initializes a new foreign key field. The target
	 * parameter is used to determine the class of the 
	 * linked model in order to create the correct 
	 * constraints. 
	 * 
	 * @param target	Class of the linked model. 
	 */
	public ForeignKeyField(Class<T> target) {
		mTarget = target;
		mOnDeleteCascade = true;
	}
	
	/**
	 * By default foreign key fields have the option
	 * ON DELETE CASCADE set. This means if the model 
	 * this field is pointing to will be deleted, also 
	 * the referencing model will be deleted. 
	 * <br /><br />
	 * 
	 * By calling doNotCascade on the field you can 
	 * prevent this behavior. 
	 */
	public void doNotCascade() {
		mOnDeleteCascade = false;
	}
	
	/**
	 * As foreign keys may require database access in order to 
	 * get their value, you have to use this function. The
	 * regular {@link DataField#get()} won't work at all times. 
	 * 
	 * @param context	{@link Context} of the application.
	 * 
	 * @return	An instance of the references model or <code>null</code>
	 * 			if nothing could be found. 
	 */
	public T get(Context context) {
		if(mValue == null) {
			return Model.objects(context, mTarget).get(mReference);
		}
		
		return mValue;
	}

	/**
	 * Creates the foreign key constraint for the database. 
	 * 
	 * @param fieldName name of the field, that references the
	 * 					other table.
	 * 
	 * @return The constraint. 
	 */
	public String getConstraint(String fieldName) {
		String constraint = "FOREIGN KEY (" + fieldName + ") " +
			"REFERENCES " + DatabaseBuilder.getTableName(mTarget) + " (" + Model.PK + ")";
		
		if(mOnDeleteCascade) {
			constraint += " ON DELETE CASCADE"; 
		} else {
			constraint += " ON DELETE SET NULL";
		}
		
		return constraint;
	}
	
	@Override
	public String getDefinition(String fieldName) {
		return new IntegerField().getDefinition(fieldName);
	}
	
	@Override
	public Class<T> getTarget() {
		return mTarget;
	}
	
	/**
	 * This method can be called in order to determine, if the
	 * referenced model has already been persisted to the database. 
	 * 
	 * @return 	<code>true</code> if the model is present in 
	 * 			the database, <code>false</code> otherwise.
	 */
	public boolean isPersisted() {
		if((mValue != null && mValue.getId() != 0) || mReference != 0) {
			return true;
		}
		
		return false;
	}

	@Override
	public void putData(String key, ContentValues values) {
		values.put(key, mReference);
	}
	
	/**
	 * When models are deleted you may wish to also release
	 * all references to other models on the instance in order
	 * to avoid unexpected behavior.
	 * <br /><br />
	 * To do this on a foreign key, call this function. 
	 */
	@Override
	public void reset() {
		mValue = null;
		mReference = 0;
	}
	
	@Override
	public void set(Cursor c, String fieldName) {
		set(c.getInt(c.getColumnIndexOrThrow(fieldName)));
	}

	/**
	 * As an alternative you don't have to hand in an instance of
	 * the model class, this field references. You can also only
	 * provide its id. 
	 * 
	 * @param id	{@link Model#PK} of the referenced model.
	 */
	public void set(int id) {
		mReference = id;
	}

	@Override
	public void set(T value) {
		mReference = value.getId();
		
		super.set(value);
	}

}
