package com.orm.androrm.test.definition;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTest extends TestSuite {
	
	public static Test suite() {
		TestSuite suite = new TestSuite();
		
		suite.addTestSuite(TableDefinitionTest.class);
		
		return suite;
	}

}
