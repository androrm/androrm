/**
 * 
 */
package com.orm.androrm;

/**
 * @author Philipp Giese
 */
public class AndStatement extends ComposedStatement{
	
	public AndStatement(String key, String value) {
		super(key, value);
	}
	
	public AndStatement(Statement left, Statement right) {
		super(left, right);
		
		mSeparator = " AND ";
	}
}
