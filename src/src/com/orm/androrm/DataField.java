package com.orm.androrm;

import android.content.Context;

public abstract class DataField<T> implements DatabaseField<T> {
	
	protected String mType;
	protected T mValue;
	protected int mMaxLength;
	
	@Override
	public String getDefinition(String fieldName) {
		return fieldName + " " + mType + "(" + mMaxLength + ")";
	}
	
	@Override
	public T get(Context context) {
		return mValue;
	}
	
	@Override
	public void set(T value) {
		mValue = value;
	}
	
	@Override
	public String toString() {
		return String.valueOf(mValue);
	}
}
