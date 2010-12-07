package com.orm.androrm.impl;

import android.content.Context;

import com.orm.androrm.CharField;
import com.orm.androrm.ForeignKeyField;
import com.orm.androrm.Model;

public class Branch extends Model {

	protected CharField mName;
	protected ForeignKeyField<Supplier> mSupplier;
	
	public Branch(Context context) {
		super(context);

		mName = new CharField(50);
		mSupplier = new ForeignKeyField<Supplier>(Supplier.class);
	}

	public void setName(String name) {
		mName.set(name);
	}
}
