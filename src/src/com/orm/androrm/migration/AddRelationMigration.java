package com.orm.androrm.migration;

import android.content.Context;

import com.orm.androrm.Model;
import com.orm.androrm.field.XToManyRelation;

public class AddRelationMigration<L extends Model, R extends Model> implements Migratable<L> {

	public AddRelationMigration(String name, XToManyRelation<L, R> field) {
		
	}

	@Override
	public boolean execute(Context context, Class<L> model) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getValue(Class<L> model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAction() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
