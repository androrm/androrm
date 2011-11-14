package com.orm.androrm.test.statement;

import java.util.Set;

import android.test.AndroidTestCase;

import com.orm.androrm.LikeStatement;

public class LikeStatementTest extends AndroidTestCase {

	public void testPlainStatement() {
		LikeStatement like = new LikeStatement("foo", "bar");
		assertEquals("foo LIKE '%bar%'", like.toString());
	}
	
	public void testMatchBeginning() {
		LikeStatement like = new LikeStatement("^foo", "bar");
		assertEquals("foo LIKE 'bar%'", like.toString());
	}
	
	public void testGetKeys() {
		LikeStatement like = new LikeStatement("foo", "bar");
		
		Set<String> keys = like.getKeys();
		
		assertEquals(1, keys.size());
		assertTrue(keys.contains("foo"));
	}
}
