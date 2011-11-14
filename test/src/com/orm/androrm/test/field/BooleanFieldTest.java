package com.orm.androrm.test.field;

import android.test.AndroidTestCase;

import com.orm.androrm.BooleanField;

public class BooleanFieldTest extends AndroidTestCase {

	public void testDefaults() {
		BooleanField b = new BooleanField();
		
		assertEquals("foo integer(1)", b.getDefinition("foo"));
		assertFalse(b.get());
	}
	
	public void testSetAndGet() {
		BooleanField b = new BooleanField();
		b.set(true);
		
		assertTrue(b.get());
	}
}
