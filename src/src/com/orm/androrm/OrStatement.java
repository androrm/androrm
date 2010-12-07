/**
 * 
 */
package com.orm.androrm;

/**
 * @author Philipp Giese
 */
public class OrStatement extends ComposedStatement {
	
	public OrStatement(Statement left, Statement right) {
		super(left, right);
		
		mSeparator = " OR ";
	}
	
	public OrStatement(String key, String value) {
		super(key, value);
	}
}
