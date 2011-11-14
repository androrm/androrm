package com.orm.androrm.test.statement;

import android.test.AndroidTestCase;

import com.orm.androrm.JoinStatement;
import com.orm.androrm.Limit;
import com.orm.androrm.Model;
import com.orm.androrm.SelectStatement;
import com.orm.androrm.Statement;
import com.orm.androrm.Where;

public class SelectStatementTest extends AndroidTestCase {

	private SelectStatement mSelect;
	
	@Override
	public void setUp() {
		mSelect = new SelectStatement();
		mSelect.from("table");
	}
	
	public void testDefault() {
		assertEquals("SELECT * FROM table", mSelect.toString());
	}
	
	public void testSelect() {
		mSelect.select("field1", "field2");
		
		assertEquals("SELECT field1, field2 FROM table", mSelect.toString());
	}
	
	public void testDistinct() {
		mSelect.distinct();
		
		assertEquals("SELECT DISTINCT * FROM table", mSelect.toString());
	}
	
	public void testFromJoin() {
		JoinStatement join = new JoinStatement();
		join.left("left_table", "a")
			.right("right_table", "b")
			.on("field1", "field2");
		
		mSelect.from(join);
		
		assertEquals("SELECT * FROM (SELECT * FROM left_table) AS a JOIN (SELECT * FROM right_table) AS b ON a.field1=b.field2", mSelect.toString());
	}
	
	public void testFromSelect() {
		SelectStatement select = new SelectStatement();
		select.from("another_table");
		
		mSelect.from(select);
		
		assertEquals("SELECT * FROM (SELECT * FROM another_table)", mSelect.toString());
	}
	
	public void testWhere() {
		Where where = new Where();
		where.setStatement(new Statement("foo", "bar"));
		
		mSelect.where(where);
		
		assertEquals("SELECT * FROM table WHERE foo = 'bar'", mSelect.toString());
	}
	
	public void testOrderBy() {
		mSelect.orderBy("column");
		
		assertEquals("SELECT * FROM table ORDER BY UPPER(column) ASC", mSelect.toString());
	}
	
	public void testLimit() {
		mSelect.limit(new Limit(10));
		
		assertEquals("SELECT * FROM table LIMIT 10", mSelect.toString());
	}
	
	public void testCount() {
		mSelect.count();
		
		assertEquals("SELECT COUNT(*) AS " + Model.COUNT + " FROM table", mSelect.toString());
	}
}
