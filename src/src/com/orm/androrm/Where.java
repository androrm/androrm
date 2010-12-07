/**
 * 
 */
package com.orm.androrm;

import java.util.HashSet;
import java.util.Set;

import android.database.sqlite.SQLiteDatabase;

/**
 * Class representing the WHERE clause of the database. 
 * <br /><br />
 * NOTE THAT THIS CLASS WORKS ACCORDING TO THE RULES DEFINED
 * BY THE query FUNCTION OF {@link SQLiteDatabase}.
 * 
 * @author Philipp Giese
 */
public class Where {
	/**
	 * All constraints that shall be applied.
	 */
	private Statement mStatement;
	/**
	 * Used for quick look up, if the where clause has a certain constraint for
	 * a given key.
	 */
	private Set<String> mKeys;
	/**
	 * Optional ORDER BY statement that can be attached to this where clause.
	 * <br /><br />
	 * NOTE THAT THIS ORDER BY HAS TO BE EXPLICITLY GATHERED IN ORDER TO 
	 * DO ITS WORK. IT WILL NOT BE ATTACHED TO THE WHERE CLAUSE AUTOMATICALLY
	 * AS THIS WOULD SCREW UP THE DATABASE QUERY.
	 */
	private String mOrderBy;
	
	public Where() {
		mKeys = new HashSet<String>();
	}
	
	/**
	 * Overwrite or set the current statement represented by this
	 * where clause. In order to create AND or OR statements rather
	 * use the respective functions. 
	 * 
	 * @param stmt {@link Statement} to apply.
	 */
	public void setStatement(Statement stmt) {
		mKeys = stmt.getKeys();
		mStatement = stmt;
	}
	
	/**
	 * Adds an AND constraint.
	 * 
	 * @param key	Name of the table column.
	 * @param value	Value of this column.
	 */
	public Where and(String key, String value) {
		return and(new Statement(key, value));
	}
	
	/**
	 * See {@link Where#and(String, String)}. 
	 */
	public Where and(String key, int value) {
		return and(key, String.valueOf(value));
	}
	
	/**
	 * Attaches the given {@link Statement} as the right side of the
	 * AND statement.
	 * 
	 * @param stmt {@link Statement} to attach.
	 * @return <code>this</code> for chaining.
	 */
	public Where and(Statement stmt) {
		mKeys.addAll(stmt.getKeys());
		
		if(mStatement == null) {
			mStatement = stmt;
		} else {
			mStatement = new AndStatement(mStatement, stmt);
		}
		
		return this;
	}
	
	/**
	 * Adds and OR constraint.
	 * 
	 * @param key Name of the table column.
	 * @param value Expected value of the column.
	 * @return <code>this</code> for chaining.
	 */
	public Where or(String key, String value) {
		return or(new Statement(key, value));
	}
	
	/**
	 * See {@link Where#or(String, String)}.
	 */
	public Where or(String key, int value) {
		return or(key, String.valueOf(value));
	}
	
	/**
	 * Attaches the given {@link Statement} as the right side of the
	 * OR statement.
	 * 
	 * @param stmt {@link Statement} to attach.
	 * @return <code>this</code> for chaining.
	 */
	public Where or(Statement stmt) {
		mKeys.addAll(stmt.getKeys());
		
		if(mStatement == null) {
			mStatement = stmt;
		} else {
			mStatement = new OrStatement(mStatement, stmt);
		}
		
		return this;
	}
	
	/**
	 * Get the value for the ORDER BY clause.
	 * 
	 * @return <code>null</code> of not set, otherwise the statement. 
	 */
	public String getOrderBy() {
		return mOrderBy;
	}
	
	/**
	 * Add an ORDER BY statement. For convenience ASC and DESC can
	 * be toggled by adding a preceding a <code>+</code> or <code>-</code>
	 * to the table column. 
	 * <br /><br />
	 * For example <code>-foo</code> will result in <code>foo DESC</code>.
	 * <br /><br />
	 * If no preceding <code>+</code> or <code>-</code> is given 
	 * <code>ASC</code> is assumed.
	 * 
	 * @param col Name of the table column.
	 */
	public void orderBy(String col) {
		if(col.startsWith("-")) {
			mOrderBy = col.substring(1) + " DESC";
		} else if(col.startsWith("+")) {
			mOrderBy = col.substring(1) + " ASC";
		} else {
			mOrderBy = col + " ASC";
		}
	}
	
	/**
	 * Checks if there is a constraint for the given column.
	 * 
	 * @param key	Column name.
	 * @return True if one exists. False otherwise.
	 */
	public boolean hasConstraint(String key) {
		return mKeys.contains(key);
	}
	
	@Override
	public String toString() {
		if(mStatement != null) {
			return mStatement.toString();
		}
		
		return null;
	}
}
