/**
 * 	Copyright (c) 2010 Philipp Giese
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
	
	public Where() {
		mKeys = new HashSet<String>();
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
	 * See {@link Where#and(String, String)}. 
	 */
	public Where and(String key, int value) {
		return and(key, String.valueOf(value));
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
	 * Checks if there is a constraint for the given column.
	 * 
	 * @param key	Column name.
	 * @return True if one exists. False otherwise.
	 */
	public boolean hasConstraint(String key) {
		return mKeys.contains(key);
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
	 * See {@link Where#or(String, String)}.
	 */
	public Where or(String key, int value) {
		return or(key, String.valueOf(value));
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
	
	public Statement getStatement() {
		return mStatement;
	}
	
	@Override
	public String toString() {
		if(mStatement != null) {
			return " WHERE " + mStatement;
		}
		
		return null;
	}
}
