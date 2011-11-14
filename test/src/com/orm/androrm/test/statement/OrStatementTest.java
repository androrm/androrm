package com.orm.androrm.test.statement;

import com.orm.androrm.AndStatement;
import com.orm.androrm.LikeStatement;
import com.orm.androrm.OrStatement;
import com.orm.androrm.Statement;

import android.test.AndroidTestCase;

public class OrStatementTest extends AndroidTestCase {

	public void testSimpleOr() {
		Statement left = new Statement("foo", "bar");
		Statement right = new Statement("bar", "baz");
		
		OrStatement or = new OrStatement(left, right);
		
		assertEquals("(foo = 'bar' OR bar = 'baz')", or.toString());
	}
	
	public void testParanthesis() {
		OrStatement left = new OrStatement(new Statement("foo", "bar"), new Statement("bar", "baz"));
		OrStatement right = new OrStatement(new Statement("baz", "foo"), new LikeStatement("baz", "bar"));
		
		AndStatement and = new AndStatement(left, right);
		
		assertEquals("(foo = 'bar' OR bar = 'baz') AND (baz = 'foo' OR baz LIKE '%bar%')", and.toString());
	}
	
	public void testParanthesisSurrounding() {
		AndStatement left = new AndStatement(new Statement("foo", "bar"), new Statement("bar", "baz"));
		AndStatement right = new AndStatement(new Statement("baz", "foo"), new LikeStatement("baz", "bar"));
		
		OrStatement or = new OrStatement(left, right);
		
		assertEquals("(foo = 'bar' AND bar = 'baz' OR baz = 'foo' AND baz LIKE '%bar%')", or.toString());
	}
}
