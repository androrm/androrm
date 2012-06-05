package com.orm.androrm.migration;

import android.content.Context;

import com.orm.androrm.AndrormField;
import com.orm.androrm.DatabaseBuilder;
import com.orm.androrm.Filter;
import com.orm.androrm.Model;

public abstract class AndrormMigration<T extends AndrormField> {

	protected static String ACTION;
	
	protected String mName;
	protected T mFieldInstance;
	
	public AndrormMigration(String name, T field) {
		mName = name;
		mFieldInstance = field;
	}
	
	public abstract boolean execute(Class<? extends Model> model, Context context);
	
	public Filter getFilter(Class<? extends Model> model) {
		Filter filter = new Filter();
		
		filter.is("mModel", DatabaseBuilder.getTableName(model))
			  .is("mAction", ACTION)
			  .is("mFieldName", mName);
		
		return filter;
	}
	
	public String getAction() {
		return ACTION;
	}
	
	public String getFieldName() {
		return mName;
	}
	
}
