package com.orm.androrm.test.statement;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTest extends TestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite();
		
		suite.addTestSuite(AndStatementTest.class);
		suite.addTestSuite(DeleteStatementTest.class);
		suite.addTestSuite(InStatementTest.class);
		suite.addTestSuite(JoinStatementTest.class);
		suite.addTestSuite(LikeStatementTest.class);
		suite.addTestSuite(LimitTest.class);
		suite.addTestSuite(OrderByTest.class);
		suite.addTestSuite(OrStatementTest.class);
		suite.addTestSuite(SelectStatementTest.class);
		suite.addTestSuite(StatementTest.class);
		suite.addTestSuite(WhereTest.class);
		
		return suite;
	}
}
