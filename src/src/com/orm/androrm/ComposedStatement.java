/**
 * 
 */
package com.orm.androrm;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Philipp Giese
 */
public abstract class ComposedStatement extends Statement {

	/**
	 * Left side of the {@link Statement}.
	 */
	protected Statement mLeft;
	/**
	 * Right side of the {@link Statement}.
	 */
	protected Statement mRight;
	/**
	 * String that separates the left side of this statement
	 * from the right side.
	 */
	protected String mSeparator;
	
	public ComposedStatement(Statement left, Statement right) {
		mLeft = left;
		mRight = right;
	}
	
	public ComposedStatement(String key, String value) {
		super(key, value);
	}
	
	@Override
	public Set<String> getKeys() {
		Set<String> keys = new HashSet<String>();
		
		keys.addAll(mLeft.getKeys());
		keys.addAll(mRight.getKeys());
		
		return keys;
	}
	
	@Override
	public String toString() {
		if(mRight != null) {
			return mLeft.toString() + mSeparator + mRight.toString();
		}
		
		if(mLeft != null) {
			return mLeft.toString();
		}
		
		return super.toString();
	}
}
