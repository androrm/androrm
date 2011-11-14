package com.orm.androrm.test.implementation;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTest extends TestSuite {
	public static Test suite() {
		TestSuite suite = new TestSuite();
		
		suite.addTestSuite(FieldResulutionTest.class);
		suite.addTestSuite(QuerySetTest.class);
		suite.addTestSuite(FilterTest.class);
		
		return suite;
	}
}
