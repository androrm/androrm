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


public class ForeignKeyField<T extends Model> extends DataField<T> implements Relation {

	private Class<T> mTarget;
	private int mReference;
	private boolean mOnDeleteCascade;
	
	public ForeignKeyField(Class<T> target) {
		mTarget = target;
		mOnDeleteCascade = true;
	}
	
	public void doNotCascade() {
		mOnDeleteCascade = false;
	}
	
	@Override
	public String getDefinition(String fieldName) {
		return new IntegerField().getDefinition(fieldName);
	}

	public String getConstraint(String fieldName) {
		String constraint = "FOREIGN KEY (" + fieldName + ") " +
			"REFERENCES " + Model.getTableName(mTarget) + " (" + Model.PK + ")";
		
		if(mOnDeleteCascade) {
			constraint += " ON DELETE CASCADE"; 
		}
		
		return constraint;
	}
	
	public boolean isPersisted() {
		if((mValue != null && mValue.getId() != 0) || mReference != 0) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public Class<? extends Model> getTarget() {
		return mTarget;
	}
	
	public T get(Context context) {
		if(mValue == null) {
			return Model.get(context, mTarget, mReference);
		}
		
		return mValue;
	}

	public void release() {
		mValue = null;
		mReference = 0;
	}
	
	public void set(int id) {
		mReference = id;
	}
	
	@Override
	public void set(T value) {
		mReference = value.getId();
		
		super.set(value);
	}

	@Override
	public void set(Cursor c, int columnIndex) {
		set(c.getInt(columnIndex));
	}

	@Override
	public void putData(String key, ContentValues values) {
		values.put(key, mReference);
	}

}
