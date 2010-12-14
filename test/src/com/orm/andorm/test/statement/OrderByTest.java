package com.orm.andorm.test.statement;

import com.orm.androrm.OrderBy;

import android.test.AndroidTestCase;

public class OrderByTest extends AndroidTestCase {

	public void testDefault() {
		OrderBy o = new OrderBy("col");
		
		assertEquals(" ORDER BY col ASC", o.toString());
	}
	
	public void testASC() {
		OrderBy o = new OrderBy("+col");
		
		assertEquals(" ORDER BY col ASC", o.toString());
	}
	
	public void testDESC() {
		OrderBy o = new OrderBy("-col");
		
		assertEquals(" ORDER BY col DESC", o.toString());
	}
	
	public void testMultiple() {
		OrderBy o = new OrderBy("col1", "-col2", "+col3");
		
		assertEquals(" ORDER BY col1 ASC, col2 DESC, col3 ASC", o.toString());
	}
}
