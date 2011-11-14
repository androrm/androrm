/**
 * 
 */
package com.orm.androrm.test.statement;

import android.test.AndroidTestCase;

import com.orm.androrm.Limit;

/**
 * @author Philipp Giese
 */
public class LimitTest extends AndroidTestCase {
	
	public void testLimitOnly() {
		Limit limit = new Limit(2);
		
		assertEquals(2, limit.getComputedLimit());
		assertEquals(" LIMIT 2", limit.toString());
	}
	
	public void testOffsetAndLimit() {
		Limit limit = new Limit(5, 10);
		
		assertEquals(15, limit.getComputedLimit());
		assertEquals(5, limit.getOffset());
		
		assertEquals(" LIMIT 5 , 15", limit.toString());
	}
	
	public void testEmptyLimit() {
		Limit limit = new Limit();
		
		assertNull(limit.toString());
	}
}
