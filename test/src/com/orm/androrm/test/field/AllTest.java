package com.orm.androrm.test.field;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTest extends TestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite();
		
		suite.addTestSuite(BooleanFieldTest.class);
		suite.addTestSuite(CharFieldTest.class);
		suite.addTestSuite(DateFieldTest.class);
		suite.addTestSuite(DoubleFieldTest.class);
		suite.addTestSuite(ForeignKeyFieldTest.class);
		suite.addTestSuite(IntegerFieldTest.class);
		suite.addTestSuite(LocationFieldTest.class);
		suite.addTestSuite(ManyToManyFieldTest.class);
		suite.addTestSuite(OneToManyFieldTest.class);
		
		return suite;
	}
}
