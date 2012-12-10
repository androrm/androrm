package com.orm.androrm.impl;

import java.util.Date;

import android.content.Context;
import android.location.Location;

import com.orm.androrm.Model;
import com.orm.androrm.QuerySet;
import com.orm.androrm.field.CharField;
import com.orm.androrm.field.DateField;
import com.orm.androrm.field.LocationField;

public class BlankModel extends Model {

	public static final QuerySet<BlankModel> objects(Context context) {
		return objects(context, BlankModel.class);
	}
	
	protected CharField mName;
	protected LocationField mLocation;
	protected DateField mDate;
	
	public BlankModel() {
		super();
		
		mName = new CharField();
		mLocation = new LocationField();
		mDate = new DateField();
	}
	
	public void setName(String name) {
		mName.set(name);
	}
	
	public String getName() {
		return mName.get();
	}
	
	public void setLocation(Location l) {
		mLocation.set(l);
	}
	
	public Location getLocation() {
		return mLocation.get();
	}
	
	public Date getDate() {
		return mDate.get();
	}
	
	public void setDate(Date date) {
		mDate.set(date);
	}
}
