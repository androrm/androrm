package com.orm.andorm.test;

import com.orm.androrm.Filter;
import com.orm.androrm.Statement;

import android.test.AndroidTestCase;

public class FilterTest extends AndroidTestCase {

	public void testFilter() {
		Statement s = new Statement("bar", "baz");
		Filter f = new Filter("foo", s);
		
		assertEquals("foo", f.getKey());
		assertEquals(s, f.getStatement());
	}

}
