/**
 * 
 */
package com.orm.androrm.test.statement;

import android.test.AndroidTestCase;

import com.orm.androrm.Where;

/**
 * @author Philipp Giese
 */
public class WhereTest extends AndroidTestCase {

	public void testEmptyWhere() {
		Where where = new Where();
		
		assertNull(where.toString());
	}
	
	public void testSimpleConstraint() {
		Where where = new Where();
		where.and("foo", "bar");
		
		assertEquals(" WHERE foo = 'bar'", where.toString());
		
		where = new Where();
		where.or("foo", "bar");
		
		assertEquals(" WHERE foo = 'bar'", where.toString());
	}
	
	public void testAddAnd() {
		Where where = new Where();
		where.and("foo", "bar")
			.and("bar", "baz");
		
		assertEquals(" WHERE foo = 'bar' AND bar = 'baz'", where.toString());
	}
	
	public void testAddOr() {
		Where where = new Where();
		where.or("foo", "bar")
			.or("bar", "baz");
		
		assertEquals(" WHERE (foo = 'bar' OR bar = 'baz')", where.toString());
	}
	
	public void testHasConstraint() {
		Where where = new Where();
		where.and("foo", "bar")
			.or("bar", "baz");
		
		assertTrue(where.hasConstraint("foo"));
		assertTrue(where.hasConstraint("bar"));
	}
}
