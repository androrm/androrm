package com.orm.androrm.impl.migration;

import com.orm.androrm.CharField;
import com.orm.androrm.Model;

public class OneFieldModel extends Model {

	protected CharField mName;
	
	public OneFieldModel() {
		super();
		
		mName = new CharField();
	}
	
}
