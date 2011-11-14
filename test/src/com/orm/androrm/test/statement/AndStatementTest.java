package com.orm.androrm.test.statement;

import java.util.Set;

import com.orm.androrm.AndStatement;
import com.orm.androrm.Statement;

import android.test.AndroidTestCase;

public class AndStatementTest extends AndroidTestCase {

	public void testSimpleAnd() {
		Statement left = new Statement("foo", "bar");
		Statement right = new Statement("bar", "baz");
		
		AndStatement and = new AndStatement(left, right);
		
		assertEquals("foo = 'bar' AND bar = 'baz'", and.toString());
	}
	
	public void testGetKey() {
		AndStatement and = new AndStatement(new Statement("foo", "bar"), new Statement("bar", "baz"));
		
		Set<String> keys = and.getKeys();
		
		assertEquals(2, keys.size());
		assertTrue(keys.contains("foo"));
		assertTrue(keys.contains("bar"));
	}
}
