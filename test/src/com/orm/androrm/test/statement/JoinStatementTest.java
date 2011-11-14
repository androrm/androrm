package com.orm.androrm.test.statement;

import android.test.AndroidTestCase;

import com.orm.androrm.JoinStatement;
import com.orm.androrm.SelectStatement;

public class JoinStatementTest extends AndroidTestCase {

	public void testPlainTable() {
		JoinStatement join = new JoinStatement();
		join.left("left_table", "a")
			.right("right_table", "b")
			.on("field1", "field2");
		
		assertEquals("(SELECT * FROM left_table) AS a JOIN (SELECT * FROM right_table) AS b ON a.field1=b.field2", join.toString()); 
	}
	
	public void testOnSubselect() {
		SelectStatement left = new SelectStatement();
		left.from("left_table")
			.select("foo");
		
		SelectStatement right = new SelectStatement();
		right.from("right_table")
			 .select("bar");
		
		JoinStatement join = new JoinStatement();
		join.left(left, "a")
			.right(right, "b")
			.on("foo", "bar");
		
		assertEquals("(SELECT foo FROM left_table) AS a JOIN (SELECT bar FROM right_table) AS b ON a.foo=b.bar", join.toString());
	}
}
