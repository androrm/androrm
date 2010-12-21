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

import java.util.List;

/**
 * This class can be used to create an IN statement
 * for database queries. 
 * <br /><br />
 * Example: <br />
 * <pre>
 * SELECT * FROM table WHERE id IN (1,3,5)
 * </pre>
 * 
 * @author Philipp Giese
 */
public class InStatement extends Statement {

	private List<Integer> mValues;
	
	public InStatement(String key, List<Integer> values) {
		mKey = key;
		mValues = values;
	}
	
	private String getList() {
		boolean first = true;
		
		String stmt = null;
		for(Integer value: mValues) {
			if(first) {
				stmt = String.valueOf(value);
				first = false;
			} else {
				stmt += "," + value;
			}
		}
		
		return stmt;
	}
	
	@Override
	public String toString() {
		return mKey + " IN (" + getList() + ")";
	}
	
}
