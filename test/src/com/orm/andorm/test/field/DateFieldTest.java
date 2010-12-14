package com.orm.andorm.test.field;

import java.util.Calendar;
import java.util.Date;

import android.test.AndroidTestCase;

import com.orm.androrm.DateField;

public class DateFieldTest extends AndroidTestCase {

	public void testDefaults() {
		DateField d = new DateField();
		
		assertEquals("foo varchar(19)", d.getDefinition("foo"));
		assertNull(d.get());
	}
	
	public void testDateFromString() {
		DateField d = new DateField();
		String date = "2010-11-02T12:23:43";
		
		d.fromString(date);
		
		Date time = d.get();
		
		assertEquals(2010 - 1900, time.getYear());
		// somehow months start at 0
		assertEquals(11 - 1, time.getMonth());
		assertEquals(2, time.getDay());
		assertEquals(12, time.getHours());
		assertEquals(23, time.getMinutes());
		assertEquals(43, time.getSeconds());
		
		date = "sadjsdnksjdnf";
		
		d = new DateField();
		d.fromString(date);
		
		assertNull(d.get());
	}
	
	public void testGetDateString() {
		DateField d = new DateField();
		String date = "2010-11-02T12:23:43";
		d.fromString(date);
		
		assertEquals(date, d.getDateString());
	}
	
	public void testSetAndGet() {
		DateField d = new DateField();
		Calendar c = Calendar.getInstance();
		Date date = c.getTime();
		
		d.set(date);
		
		assertEquals(date, d.get());
	}
}
