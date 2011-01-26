/**
 * 	Copyright (c) 2010 Philipp Giese
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
		double lat = 0.0;
		double lng = 0.0;
		
		if(mValue != null) {
			lat = mValue.getLatitude();
			lng = mValue.getLongitude();
		}
		
		values.put(fieldName + "Lat", lat);
		values.put(fieldName + "Lng", lng);
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

	@Override
	public void reset() {
		mValue = null;
	}

}
