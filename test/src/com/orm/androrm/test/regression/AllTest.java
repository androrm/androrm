package com.orm.androrm.test.regression;

import junit.framework.TestSuite;

public class AllTest extends TestSuite {

	public static TestSuite suite() {
		TestSuite suite = new TestSuite();
		
		suite.addTestSuite(ModelRegression.class);
		suite.addTestSuite(QueryRegression.class);
		
		return suite;
	}
	
}
