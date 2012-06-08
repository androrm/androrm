package com.orm.androrm.test.cache;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTest extends TestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite();
		
		suite.addTestSuite(FieldCacheTest.class);
		suite.addTestSuite(TableDefinitionCacheTest.class);
		
		return suite;
	}
	
}
