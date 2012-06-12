package com.orm.androrm.test.field;

import android.test.AndroidTestCase;

import com.orm.androrm.field.BlobField;

public class BlobFieldTest extends AndroidTestCase {

	public void testDefaults() {
		BlobField b = new BlobField();
		
		assertEquals("`foo` blob", b.getDefinition("foo"));
		assertNull(b.get());
	}
	
	public void testSetAndGet() {
		String data = "I am the content";

		BlobField b = new BlobField();
		b.set(data.getBytes());

		byte[] retrieved = b.get();
		
		assertTrue(data.equals(new String(retrieved)));
	}
	
}
