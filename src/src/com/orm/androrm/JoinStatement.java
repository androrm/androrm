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


public class JoinStatement {
	private SelectStatement mLeft;
	private SelectStatement mRight;
	private String mLeftAlias;
	private String mRightAlias;
	private String mLeftColumn;
	private String mRightColumn;

	public JoinStatement left(SelectStatement left, String as) {
		mLeft = left;
		mLeftAlias = as;
		
		return this;
	}
	
	public JoinStatement left(String tableName, String as) {
		SelectStatement select = new SelectStatement();
		select.from(tableName);
		
		return left(select, as);
	}
	
	public JoinStatement right(SelectStatement right, String as) {
		mRight = right;
		mRightAlias = as;
		
		return this;
	}
	
	public JoinStatement right(String tableName, String as) {
		SelectStatement select = new SelectStatement();
		select.from(tableName);
		
		return right(select, as);
	}
	
	public JoinStatement on(String leftColum, String rightColumn) {
		mLeftColumn = leftColum;
		mRightColumn = rightColumn;
		
		return this;
	}
	
	private String buildStatement(int counter) {
		
		String join = "(" +
				mLeft.toString() + 
			") AS " + mLeftAlias + 
			" JOIN (" +
				mRight.toString() +
			") AS " + mRightAlias +
			" ON " + 
				mLeftAlias + "." + mLeftColumn + 
				"=" +
				mRightAlias + "." + mRightColumn;
		
		return join;
				
	}
	
	public String toString() {
		return buildStatement(0);
	}
}
