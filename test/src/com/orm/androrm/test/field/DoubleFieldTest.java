package com.orm.androrm.test.field;

import com.orm.androrm.field.DoubleField;

import android.content.ContentValues;
import android.test.AndroidTestCase;

public class DoubleFieldTest extends AndroidTestCase {

	public void testDefaults() {
		DoubleField d = new DoubleField();
		
		assertEquals("`foo` numeric", d.getDefinition("foo"));
		assertEquals(0.0, d.get());
	}
	
	public void testMods() {
		DoubleField d = new DoubleField(0);
		assertEquals("`foo` numeric", d.getDefinition("foo"));
		
		d = new DoubleField(17);
		assertEquals("`foo` numeric", d.getDefinition("foo"));
		
		d = new DoubleField(10);
		assertEquals("`foo` numeric(10)", d.getDefinition("foo"));
	}
	
	public void testSetAndGet() {
		DoubleField d = new DoubleField();
		
		d.set(27.12345);
		
		assertEquals(27.12345, d.get());
	}
	
	public void testPutData() {
		ContentValues values = new ContentValues();
		DoubleField d = new DoubleField();
		d.set(12.3);
		
		d.putData("foo", values);
		
		assertEquals(12.3, values.getAsDouble("foo"));
	}
	
	public void testReset() {
		DoubleField d = new DoubleField();
		
		d.set(12.3);
		assertEquals(12.3, d.get());
		
		d.reset();
		assertEquals(0.0, d.get());
	}
}
