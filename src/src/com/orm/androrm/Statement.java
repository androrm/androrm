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
 * This class is the superclass of all statements. The whole statement
 * structure is used to build up where complex {@link Where} clauses. The 
 * abstract grammar of a {@link Statement} is the following.
 * 
 * <pre>
 *   KEY   <b>-></b> STRING
 *   VALUE <b>-></b> STRING
 *   
 *   STMT <b>-></b> KEY <b>= '</b>VALUE<b>'</b>
 *   STMT <b>-></b> LIKE_STMT
 *   STMT <b>-></b> COMPOSED_STMT
 *   
 *   LIKE_STMT <b>-></b> KEY <b>LIKE '%</b>VALUE<b>%'</b>
 *   
 *   COMPOSED_STMT <b>-></b> AND_STMT
 *   COMPOSED_STMT <b>-></b> OR_STMT
 *   
 *   AND_STMT <b>-></b> STMT
 *   AND_STMT <b>-></b> STMT <b>AND</b> STMT
 *   
 *   OR_STMT <b>-></b> STMT
 *   OR_STMT <b>-></b> STMT <b>OR</b> STMT
 * </pre>
 * 
 * @author Philipp Giese
 */
public class Statement {
	
	/**
	 * Key of the statement.
	 */
	protected String mKey;
	/**
	 * Value of the statement.
	 */
	protected String mValue;
	
	/**
	 * Empty constructor.
	 */
	public Statement() {}
	
	public Statement(String key, int value) {
		mKey = key;
		mValue = String.valueOf(value);
	}
	
	/**
	 * This constructor sets the key and value field of this 
	 * statement.
	 * 
	 * @param key Database column.
	 * @param value Expected value of this column.
	 */
	public Statement(String key, String value) {
		mKey = key;
		mValue = value;
	}
	
	/**
	 * Get all keys that are used in this statement.
	 * 
	 * @return All keys i.e. the affected database columns.
	 */
	public Set<String> getKeys() {
		Set<String> keys = new HashSet<String>();
		keys.add(mKey);
		
		return keys;
	}
	
	public void setKey(String key) {
		mKey = key;
	}
	
	@Override
	public String toString() {
		return mKey + " = '" + mValue + "'";
	}
}
