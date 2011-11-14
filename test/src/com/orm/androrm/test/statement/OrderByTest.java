package com.orm.androrm.test.statement;

import com.orm.androrm.OrderBy;

import android.test.AndroidTestCase;

public class OrderByTest extends AndroidTestCase {

	public void testDefault() {
		OrderBy o = new OrderBy("col");
		
		assertEquals(" ORDER BY UPPER(col) ASC", o.toString());
	}
	
	public void testASC() {
		OrderBy o = new OrderBy("+col");
		
		assertEquals(" ORDER BY UPPER(col) ASC", o.toString());
	}
	
	public void testDESC() {
		OrderBy o = new OrderBy("-col");
		
		assertEquals(" ORDER BY UPPER(col) DESC", o.toString());
	}
	
	public void testMultiple() {
		OrderBy o = new OrderBy("col1", "-col2", "+col3");
		
		assertEquals(" ORDER BY UPPER(col1) ASC, UPPER(col2) DESC, UPPER(col3) ASC", o.toString());
	}
}
