package com.orm.androrm.test.field;

import com.orm.androrm.IntegerField;

import android.test.AndroidTestCase;

public class IntegerFieldTest extends AndroidTestCase {

	public void testDefauls() {
		IntegerField i = new IntegerField();
		
		assertEquals("foo integer", i.getDefinition("foo"));
		assertTrue(0 == i.get());
	}
	
	public void testMods() {
		IntegerField i = new IntegerField(0);
		assertEquals("foo integer", i.getDefinition("foo"));

		i = new IntegerField(17);
		assertEquals("foo integer", i.getDefinition("foo"));
		
		i = new IntegerField(10);
		assertEquals("foo integer(10)", i.getDefinition("foo"));
	}
	
	public void testSetAndGet() {
		IntegerField i = new IntegerField();
		
		i.set(123);
		
		assertTrue(123 == i.get());
	}
}
