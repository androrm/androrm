/**
 * 
 */
package com.orm.androrm;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Philipp Giese
 *
 */
public class DateField extends DataField<Date> {

	public DateField() {
		mType = "varchar";
		mMaxLength = 19;
	}
	
	public String getDateString() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(mValue);
		
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		
		return year + "-" + month + "-" + day
		 	+ "T"
		 	+ hour + ":" + minute + ":" + second;
	}
	
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
}
