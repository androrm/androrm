package com.orm.androrm.test;

import com.orm.androrm.Rule;
import com.orm.androrm.Statement;

import android.test.AndroidTestCase;

public class RuleTest extends AndroidTestCase {

	public void testFilter() {
		Statement s = new Statement("bar", "baz");
		Rule f = new Rule("foo", s);
		
		assertEquals("foo", f.getKey());
		assertEquals(s, f.getStatement());
	}

}
