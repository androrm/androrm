/**
 * 
 */
package com.orm.androrm;

import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;

/**
 * @author Philipp Giese
 *
 */
public class LocationField extends DataField<Location> {

	public LocationField() {
		mType = "numeric";
	}
	
	/**
	 * As a location consists of longitude and latitude,
	 * this field will create 2 fields in the database.
	 */
	@Override
	public String getDefinition(String fieldName) {
		String definition = fieldName + "Lat " + mType + ", ";
		definition += fieldName + "Lng " + mType; 
		
		return definition;
	}
	
	@Override
	public void putData(String fieldName, ContentValues values) {
		values.put(fieldName + "Lat", mValue.getLatitude());
		values.put(fieldName + "Lng", mValue.getLongitude());
	}
	
	@Override
	public void set(Cursor c, String fieldName) {
		double lat = c.getDouble(c.getColumnIndexOrThrow(fieldName + "Lat"));
		double lng = c.getDouble(c.getColumnIndexOrThrow(fieldName + "Lng"));
		
		Location l = new Location(LocationManager.GPS_PROVIDER);
		l.setLatitude(lat);
		l.setLongitude(lng);
		
		mValue = l;
	}

}
