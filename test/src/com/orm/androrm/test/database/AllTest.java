package com.orm.androrm.test.database;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTest extends TestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite();
		
		suite.addTestSuite(TransactionTest.class);
		
		return suite;
	}
	
}
