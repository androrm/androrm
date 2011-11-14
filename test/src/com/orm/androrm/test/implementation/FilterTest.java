package com.orm.androrm.test.implementation;

import android.test.AndroidTestCase;

import com.orm.androrm.Filter;
import com.orm.androrm.NoSuchFieldException;
import com.orm.androrm.impl.Brand;

public class FilterTest extends AndroidTestCase {

	public void testNoSuchFieldException() {
		Filter filter = new Filter();
		filter.is("no_such_field", "value");
		
		try {
			Brand.objects(getContext()).filter(filter);
			
			fail();
		} catch (NoSuchFieldException e) {

		}
	}
	
}
