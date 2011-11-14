package com.orm.androrm.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTest extends TestSuite {
	
	public static Test suite() {
		TestSuite suite = new TestSuite();
		
		suite.addTest(com.orm.androrm.test.definition.AllTest.suite());
		suite.addTest(com.orm.androrm.test.statement.AllTest.suite());
		suite.addTest(com.orm.androrm.test.field.AllTest.suite());
		suite.addTest(com.orm.androrm.test.implementation.AllTest.suite());
		
		suite.addTestSuite(ModelTest.class);
		suite.addTestSuite(RuleTest.class);
		suite.addTestSuite(FilterTest.class);
		
		return suite;
	}

}
