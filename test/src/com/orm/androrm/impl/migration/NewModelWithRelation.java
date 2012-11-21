package com.orm.androrm.impl.migration;

import android.content.Context;

import com.orm.androrm.Model;
import com.orm.androrm.QuerySet;
import com.orm.androrm.field.ManyToManyField;

public class NewModelWithRelation extends Model {

	public static QuerySet<NewModelWithRelation> objects(Context context) {
		return objects(context, NewModelWithRelation.class);
	}
	
	protected ManyToManyField<NewModelWithRelation, EmptyModel> mRelation;
	
	public NewModelWithRelation() {
		super();
		
		mRelation = new ManyToManyField<NewModelWithRelation, EmptyModel>(NewModelWithRelation.class, EmptyModel.class);
	}
	
	public QuerySet<EmptyModel> getRelations(Context context) {
		return mRelation.get(context, this);
	}
	
}
