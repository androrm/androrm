package com.orm.androrm.impl.migration;

import android.content.Context;

import com.orm.androrm.Model;
import com.orm.androrm.QuerySet;
import com.orm.androrm.field.ManyToManyField;

public class ModelWithRelation extends Model {

	protected ManyToManyField<ModelWithRelation, EmptyModel> mRelation;
	
	public ModelWithRelation() {
		super();
		
		mRelation = new ManyToManyField<ModelWithRelation, EmptyModel>(ModelWithRelation.class, EmptyModel.class);
	}
	
	public void addRelation(EmptyModel model) {
		mRelation.add(model);
	}
	
	public QuerySet<EmptyModel> getRelations(Context context) {
		return mRelation.get(context, this);
	}
}
