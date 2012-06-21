package com.orm.androrm.impl.migration;

import com.orm.androrm.Model;
import com.orm.androrm.field.ManyToManyField;

public class ModelWithRelation extends Model {

	protected ManyToManyField<ModelWithRelation, EmptyModel> mRelation;
	
	public ModelWithRelation() {
		super();
		
		mRelation = new ManyToManyField<ModelWithRelation, EmptyModel>(ModelWithRelation.class, EmptyModel.class);
	}
	
}
