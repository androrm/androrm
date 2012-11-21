package com.orm.androrm.impl.migration;

import android.content.Context;

import com.orm.androrm.Model;
import com.orm.androrm.QuerySet;
import com.orm.androrm.field.ManyToManyField;

public class EmptyModel extends Model {

	public static QuerySet<EmptyModel> objects(Context context) {
		return objects(context, EmptyModel.class);
	}
	
	protected ManyToManyField<EmptyModel, NewEmptyModel> mM2M;
	
	public EmptyModel() {
		super();
		
		mM2M = new ManyToManyField<EmptyModel, NewEmptyModel>(EmptyModel.class, NewEmptyModel.class);
	}
	
}
