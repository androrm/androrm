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
 * This class can be used to create LIKE statement
 * for queries on the database. 
 * <br /><br />
 * Example: <br />
 * <pre>
 * SELECT * FROM table WHERE name LIKE '%foo%'
 * </pre>
 * 
 * @author Philipp Giese
 */
public class LikeStatement extends Statement {

	private boolean mMatchBeginning = false;
	
	private static String parseKey(String key) {
		if(key.substring(0, 1).equals("^")) {
			key = key.substring(1);
		}
		
		return key;
	}
	
	public LikeStatement(String key, String value) {
		super(parseKey(key), value);
		
		if(key.substring(0, 1).equals("^")) {
			mMatchBeginning = true;
		}
	}
	
	@Override
	public String toString() {
		String stmt = mKey + " LIKE '";
		
		if(!mMatchBeginning) {
			stmt += "%";
		}
		
		stmt += mValue + "%'";
		
		return stmt;
	}

}
