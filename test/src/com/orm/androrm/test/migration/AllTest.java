package com.orm.androrm.test.migration;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTest extends TestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite();
		
		suite.addTestSuite(DataFieldMigrationTest.class);
		suite.addTestSuite(MigrationHelperTest.class);
		suite.addTestSuite(RenameModelMigrationTest.class);
		
		return suite;
	}
	
}
