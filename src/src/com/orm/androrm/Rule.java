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
 * A {@link Rule} is used by {@link Filter Filter Sets}
 * to create complex queries on the database. Each filter consists
 * of a certain key leading to the field is applied to and 
 * the {@link Statement}, that will be used for the query. 
 * 
 * @author Philipp Giese
 */
public class Rule {

	/**
	 * The key leads to the field this filter
	 * is applied on. This can be the plain name
	 * of a field like "mName" or a series of 
	 * field names like "mSupplier__mBranches__mName".
	 */
	private String mKey; 
	/**
	 * The statement of a field is only valid for the
	 * last field name in {@link Rule#mKey}. 
	 */
	private Statement mStatement;
	
	public Rule(String key, Statement statement) {
		mKey = key;
		mStatement = statement;
	}
	
	public String getKey() {
		return mKey;
	}
	
	public Statement getStatement() {
		return mStatement;
	}
}
