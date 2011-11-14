package com.orm.androrm.test.field;

import com.orm.androrm.CharField;

import android.test.AndroidTestCase;

public class CharFieldTest extends AndroidTestCase {

	public void testDefaults() {
		CharField c = new CharField();
		
		assertEquals("foo varchar", c.getDefinition("foo"));
		assertNull(c.get());
	}
	
	public void testMods() {
		CharField c = new CharField(0);
		assertEquals("foo varchar", c.getDefinition("foo"));
		
		c = new CharField(256);
		assertEquals("foo varchar", c.getDefinition("foo"));
		
		c = new CharField(50);
		assertEquals("foo varchar(50)", c.getDefinition("foo"));
	}
	
	public void testSetAndGet() {
		CharField c = new CharField();
		c.set("foo");
		
		assertEquals("foo", c.get());
	}
}
