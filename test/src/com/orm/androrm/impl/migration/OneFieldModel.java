package com.orm.androrm.impl.migration;

import com.orm.androrm.Model;
import com.orm.androrm.field.CharField;

public class OneFieldModel extends Model {

	protected CharField mName;
	
	public OneFieldModel() {
		super();
		
		mName = new CharField();
	}
	
}
