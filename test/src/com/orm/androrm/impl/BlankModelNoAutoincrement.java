package com.orm.androrm.impl;

import com.orm.androrm.Model;
import com.orm.androrm.field.CharField;

public class BlankModelNoAutoincrement extends Model {

	protected CharField mName;
	
	public BlankModelNoAutoincrement() {
		super(true);
		
		mName = new CharField();
	}
}
