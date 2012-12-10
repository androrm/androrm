package com.orm.androrm.test.regression;

import java.util.ArrayList;
import java.util.List;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.Model;
import com.orm.androrm.impl.Car;
import com.orm.androrm.impl.Person;

import android.test.AndroidTestCase;

public class ForeignKeyRegression extends AndroidTestCase {

	@Override
	public void setUp() {
		List<Class<? extends Model>> models = new ArrayList<Class<? extends Model>>();
		models.add(Car.class);
		models.add(Person.class);
		
		DatabaseAdapter adapter = DatabaseAdapter.getInstance(getContext());
		adapter.setModels(models);
	}
	
	public void testKeyNull() {
		Person person = new Person();
		person.setName("Paul");
		
		try {
			person.save(getContext());
		} catch (NullPointerException e) {
			fail();
		}
	}
	
	@Override
	public void tearDown() {
		DatabaseAdapter adapter = DatabaseAdapter.getInstance(getContext());
		adapter.drop();
	}
	
}
