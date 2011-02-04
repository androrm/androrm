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

/**
 * @author Philipp Giese
 */
public class OrderBy {

	private String mOrderBy;
	
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
	public OrderBy(String... columns) {
		boolean first = true;
		
		for(int i = 0, length = columns.length; i < length; i++) {
			String col = columns[i];
			
			if(!first) {
				mOrderBy += ", ";
			} else {
				mOrderBy = " ";
			}
			
			if(col.startsWith("-")) {
				mOrderBy += "UPPER(" + col.substring(1) + ") DESC";
			} else if(col.startsWith("+")) {
				mOrderBy += "UPPER(" + col.substring(1) + ") ASC";
			} else {
				mOrderBy += "UPPER(" + col + ") ASC";
			}
			
			if(first) {
				first = false;
			}
		}
	}
	
	@Override
	public String toString() {
		return " ORDER BY" + mOrderBy;
	}
}
