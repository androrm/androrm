package com.orm.androrm.test.field;

import com.orm.androrm.DoubleField;

import android.test.AndroidTestCase;

public class DoubleFieldTest extends AndroidTestCase {

	public void testDefaults() {
		DoubleField d = new DoubleField();
		
		assertEquals("foo numeric", d.getDefinition("foo"));
		assertEquals(0.0, d.get());
	}
	
	public void testMods() {
		DoubleField d = new DoubleField(0);
		assertEquals("foo numeric", d.getDefinition("foo"));
		
		d = new DoubleField(17);
		assertEquals("foo numeric", d.getDefinition("foo"));
		
		d = new DoubleField(10);
		assertEquals("foo numeric(10)", d.getDefinition("foo"));
	}
	
	public void testSetAndGet() {
		DoubleField d = new DoubleField();
		
		d.set(27.12345);
		
		assertEquals(27.12345, d.get());
	}
}
