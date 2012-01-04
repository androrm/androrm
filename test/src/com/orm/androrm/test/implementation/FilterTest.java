package com.orm.androrm.test.implementation;

import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.Filter;
import com.orm.androrm.Model;
import com.orm.androrm.NoSuchFieldException;
import com.orm.androrm.impl.Brand;

public class FilterTest extends AndroidTestCase {

	public void setUp() {
		List<Class<? extends Model>> models = new ArrayList<Class<? extends Model>>();
		models.add(Brand.class);
		
		DatabaseAdapter adapter = new DatabaseAdapter(getContext());
		adapter.setModels(models);
	}
	
	public void testNoSuchFieldException() {
		Filter filter = new Filter();
		filter.is("no_such_field", "value");
		
		try {
			Brand.objects(getContext()).filter(filter);
			
			fail();
		} catch (NoSuchFieldException e) {

		}
	}
	
	public void tearDown() {
		DatabaseAdapter adapter = new DatabaseAdapter(getContext());
		adapter.drop();
	}
}
