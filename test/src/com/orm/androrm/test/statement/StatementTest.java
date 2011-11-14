package com.orm.androrm.test.statement;

import java.util.Set;

import android.test.AndroidTestCase;

import com.orm.androrm.Statement;

public class StatementTest extends AndroidTestCase {

	public void testPlainStatement() {
		Statement stmt = new Statement("foo", "bar");
		assertEquals("foo = 'bar'", stmt.toString());
	}
	
	public void testGetKeys() {
		Statement stmt = new Statement("foo", "bar");
		
		Set<String> keys = stmt.getKeys();
		
		assertEquals(keys.size(), 1);
		assertTrue(keys.contains("foo"));
	}
}
