package com.orm.androrm.impl.migration;

import com.orm.androrm.Model;
import com.orm.androrm.field.ManyToManyField;

public class EmptyModel extends Model {

	protected ManyToManyField<EmptyModel, NewEmptyModel> mM2M;
	
	public EmptyModel() {
		super();
		
		mM2M = new ManyToManyField<EmptyModel, NewEmptyModel>(EmptyModel.class, NewEmptyModel.class);
	}
	
}
