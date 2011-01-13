package com.orm.androrm.impl;

import android.location.Location;

import com.orm.androrm.CharField;
import com.orm.androrm.LocationField;
import com.orm.androrm.Model;

public class BlankModel extends Model {

	protected CharField mName;
	protected LocationField mLocation;
	
	public BlankModel() {
		super();
		
		mName = new CharField();
		mLocation = new LocationField();
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
}
