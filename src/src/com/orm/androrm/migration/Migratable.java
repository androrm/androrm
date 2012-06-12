package com.orm.androrm.migration;

import android.content.Context;

import com.orm.androrm.Model;

public interface Migratable<T extends Model> {

	public boolean execute(Context context, Class<T> model);
	
	public String getValue(Class<T> model);
	
	public String getAction();
	
}
