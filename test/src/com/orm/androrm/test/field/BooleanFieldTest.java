package com.orm.androrm.test.field;

import android.content.ContentValues;
import android.test.AndroidTestCase;

import com.orm.androrm.field.BooleanField;

public class BooleanFieldTest extends AndroidTestCase {

	public void testDefaults() {
		BooleanField b = new BooleanField();
		
		assertEquals("`foo` integer(1)", b.getDefinition("foo"));
		assertFalse(b.get());
	}
	
	public void testSetAndGet() {
		BooleanField b = new BooleanField();
		b.set(true);
		
		assertTrue(b.get());
	}
	
	public void testPutData() {
		BooleanField b = new BooleanField();
		ContentValues values = new ContentValues();
		
		b.set(true);
		b.putData("foo", values);
		
		assertTrue(values.getAsBoolean("foo"));
	}
	
	public void testReset() {
		BooleanField b = new BooleanField();
		
		b.set(true);
		assertTrue(b.get());
		
		b.reset();
		assertFalse(b.get());
	}
}
