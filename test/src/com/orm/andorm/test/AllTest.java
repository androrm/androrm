package com.orm.andorm.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTest extends TestSuite {
	
	public static Test suite() {
		TestSuite suite = new TestSuite();
		
		suite.addTest(com.orm.andorm.test.definition.AllTest.suite());
		suite.addTest(com.orm.andorm.test.statement.AllTest.suite());
		suite.addTest(com.orm.andorm.test.field.AllTest.suite());
		
		suite.addTestSuite(ModelTest.class);
		suite.addTestSuite(FilterTest.class);
		suite.addTestSuite(FilterSetTest.class);
		
		return suite;
	}

}
