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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * This class represents a {@link Date} field. 
 * 
 * @author Philipp Giese
 */
public class DateField extends DataField<Date> {

	/**
	 * Initializes this field. Note, that dates will be
	 * stored as strings into the database. Therefore
	 * the database type of this field is <b>varchar</b>
	 * and it's length is set to 19 characters, as this
	 * is the exact length of the date string.
	 */
	public DateField() {
		mType = "varchar";
		mMaxLength = 19;
	}
	
	/**
	 * Constructs a {@link Date} object from the given string. <br />
	 * The String must be in the format: YYYY-MM-DDTHH:MM:SS.
	 * 
	 * @param date	String representing the date.
	 */
	public void fromString(String date) {
		Pattern pattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2})");
		
		if(date != null) {
			Matcher matcher = pattern.matcher(date);
			
			if(matcher.matches()) {
				int year = Integer.valueOf(matcher.group(1));
				int month = Integer.valueOf(matcher.group(2)) - 1;
				int day = Integer.valueOf(matcher.group(3));
				int hour = Integer.valueOf(matcher.group(4));
				int minute = Integer.valueOf(matcher.group(5));
				int second = Integer.valueOf(matcher.group(6));
				
				GregorianCalendar cal = new GregorianCalendar(year, month, day, hour, minute, second);
				
				mValue = cal.getTime();
			}
		}
	}
	
	/**
	 * Creates the string representation of the date
	 * {@link DataField#mValue} is currently set to. 
	 * <br /><br />
	 * Format of that string is:<br />
	 * YYYY-MM-DDTHH:MM:SS
	 * 
	 * @return	String representation of {@link DataField#mValue}.
	 */
	public String getDateString() {
		if(mValue != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(mValue);
			
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1;
			int day = cal.get(Calendar.DAY_OF_MONTH);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int minute = cal.get(Calendar.MINUTE);
			int second = cal.get(Calendar.SECOND);
			
			String dayString = String.valueOf(day);
			String monthString = String.valueOf(month);
			String hourString = String.valueOf(hour);
			String minuteString = String.valueOf(minute);
			String secondString = String.valueOf(second);
			
			if(day < 10) {
				dayString = "0" + dayString;
			}
			
			if(month < 10) {
				monthString = "0" + monthString;
			}
			
			if(hour < 10) {
				hourString = "0" + hourString;
			}
			
			if(minute < 10) {
				minuteString = "0" + minuteString;
			}
			
			if(second < 10) {
				secondString = "0" + secondString;
			}
			
			return year + "-" + monthString + "-" + dayString
			 	+ "T"
			 	+ hourString + ":" + minuteString + ":" + secondString;
		}
		
		return null;
	}

	@Override
	public void putData(String key, ContentValues values) {
		values.put(key, getDateString());
	}

	@Override
	public void set(Cursor c, String fieldName) {
		fromString(c.getString(c.getColumnIndexOrThrow(fieldName)));
	}

	@Override
	public void reset() {
		mValue = null;
	}
}
