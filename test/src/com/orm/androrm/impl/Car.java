package com.orm.androrm.impl;

import android.content.Context;

import com.orm.androrm.Model;
import com.orm.androrm.QuerySet;
import com.orm.androrm.field.CharField;
import com.orm.androrm.field.OneToManyField;

public class Car extends Model {

	public static final QuerySet<Car> objects(Context context) {
		return objects(context, Car.class);
	}
	
	protected OneToManyField<Car, Person> mDrivers;
	protected CharField mName;
	
	public Car() {
		super();
		
		mName = new CharField();
		mDrivers = new OneToManyField<Car, Person>(Car.class, Person.class);
	}
	
	public void setName(String name) {
		mName.set(name);
	}
	
	public void addDriver(Person driver) {
		mDrivers.add(driver);
	}
	
	public QuerySet<Person> getDrivers(Context context) {
		return mDrivers.get(context, this);
	}
	
}
