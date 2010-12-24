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

/**
 * This is the abstract superclass of all composed statements. 
 * Composed statement have a left and a right side that is also
 * a statement. Both sides are separated by a certain keyword.
 * <br /><br />
 * Examples are {@link AndStatement} and {@link OrStatement}. 
 * 
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
