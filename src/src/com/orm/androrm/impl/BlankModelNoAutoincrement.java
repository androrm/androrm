package com.orm.androrm.impl;

import com.orm.androrm.CharField;
import com.orm.androrm.Model;

public class BlankModelNoAutoincrement extends Model {

	protected CharField mName;
	
	public BlankModelNoAutoincrement() {
		super(true);
		
		mName = new CharField();
	}
}
