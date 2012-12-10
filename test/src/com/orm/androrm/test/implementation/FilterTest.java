package com.orm.androrm.test.implementation;

import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.Filter;
import com.orm.androrm.Model;
import com.orm.androrm.QuerySet;
import com.orm.androrm.field.NoSuchFieldException;
import com.orm.androrm.impl.Brand;
import com.orm.androrm.impl.Car;
import com.orm.androrm.impl.Person;

public class FilterTest extends AndroidTestCase {

	public void setUp() {
		List<Class<? extends Model>> models = new ArrayList<Class<? extends Model>>();
		models.add(Brand.class);
		models.add(Person.class);
		models.add(Car.class);
		
		DatabaseAdapter adapter = DatabaseAdapter.getInstance(getContext());
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
	
	public void testInFilterModel() {
		Person p = new Person();
		p.setName("tom");
		p.save(getContext());
		
		Car c = new Car();
		c.addDriver(p);
		c.setName("Toyota");
		c.save(getContext());
		
		List<Person> drivers = new ArrayList<Person>();
		drivers.add(p);
		
		Filter filter = new Filter();
		filter.in("mDrivers", drivers);
		
		QuerySet<Car> cars = Car.objects(getContext()).filter(filter);
		
		assertEquals(1, cars.count());
	}
	
	public void testInFilterString() {
		Person tom = new Person();
		tom.setName("tom");
		tom.save(getContext());
		
		Person peter = new Person();
		peter.setName("peter");
		peter.save(getContext());
		
		Person susan = new Person();
		susan.setName("susan");
		susan.save(getContext());
		
		List<String> names = new ArrayList<String>();
		names.add("tom");
		names.add("peter");
		
		Filter filter = new Filter();
		filter.in("mName", names);
		
		QuerySet<Person> people = Person.objects(getContext()).filter(filter);
		
		assertEquals(2, people.count());
	}
	
	public void tearDown() {
		DatabaseAdapter adapter = DatabaseAdapter.getInstance(getContext());
		adapter.drop();
	}
}
