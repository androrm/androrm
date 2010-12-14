package com.orm.androrm.impl;

import com.orm.androrm.CharField;
import com.orm.androrm.Model;

public class BlankModel extends Model {

	protected CharField mName;
	
	public BlankModel() {
		super();
		
		mName = new CharField();
	}
	
	public void setName(String name) {
		mName.set(name);
	}
	
	public String getName() {
		return mName.get();
	}
}
