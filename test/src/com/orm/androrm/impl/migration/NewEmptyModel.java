package com.orm.androrm.impl.migration;

import android.content.Context;

import com.orm.androrm.Model;
import com.orm.androrm.QuerySet;

public class NewEmptyModel extends Model {

	public static QuerySet<NewEmptyModel> objects(Context context) {
		return objects(context, NewEmptyModel.class);
	}
		
	public NewEmptyModel() {
		super();
	}
	
}
