package com.orm.androrm.impl;

import android.content.Context;

import com.orm.androrm.Model;
import com.orm.androrm.QuerySet;
import com.orm.androrm.field.CharField;
import com.orm.androrm.field.ForeignKeyField;

public class Person extends Model {

	public static final QuerySet<Person> objects(Context context) {
		return objects(context, Person.class);
	}
	
	protected ForeignKeyField<Car> mCar;
	protected CharField mName;
	
	public Person() {
		super();
		
		mName = new CharField();
		mCar = new ForeignKeyField<Car>(Car.class);
	}
	
	public void setName(String name) {
		mName.set(name);
	}
	
	public String getName() {
		return mName.get();
	}
}
