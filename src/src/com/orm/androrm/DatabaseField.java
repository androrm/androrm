package com.orm.androrm;

import android.content.Context;

public interface DatabaseField<T> {
	public String getDefinition(String fieldName);
	
	public T get(Context context);
	public void set(T value);
}
