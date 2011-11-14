package com.orm.androrm.test.statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.test.AndroidTestCase;

import com.orm.androrm.InStatement;

public class InStatementTest extends AndroidTestCase {

	public void testPlainStatement() {
		List<Integer> values = new ArrayList<Integer>();
		values.add(1);
		
		InStatement in = new InStatement("foo", values);
		assertEquals("foo IN (1)", in.toString());
		
		values.add(2);
		values.add(3);
		
		in = new InStatement("foo", values);
		assertEquals("foo IN (1,2,3)", in.toString());
	}
	
	public void testGetKeys() {
		List<Integer> values = new ArrayList<Integer>();
		values.add(1);
		
		InStatement in = new InStatement("foo", values);
		
		Set<String> keys = in.getKeys();
		
		assertEquals(1, keys.size());
		assertTrue(keys.contains("foo"));
	}
}
