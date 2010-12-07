package com.orm.androrm;

import android.content.Context;

public class ForeignKeyField<T extends Model> extends DataField<T> implements Relation {

	private Class<T> mTarget;
	private int mReference;
	
	public ForeignKeyField(Class<T> target) {
		mTarget = target;
	}
	
	@Override
	public String getDefinition(String fieldName) {
		return new IntegerField().getDefinition(fieldName);
	}

	public String getConstraint(String fieldName) {
		String constraint = "FOREIGN KEY (" + fieldName + ") " +
			"REFERENCES " + Model.getTableName(mTarget) + " (" + Model.PK + ")";
		
		return constraint;
	}
	
	public boolean isPersisted() {
		if(mValue != null && mValue.getId() != 0) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public Class<? extends Model> getTarget() {
		return mTarget;
	}
	
	@Override
	public T get(Context context) {
		if(mValue == null) {
			return Model.get(context, mTarget, mReference);
		}
		
		return mValue;
	}
	
	public void set(int id) {
		mReference = id;
	}

}
