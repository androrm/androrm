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


public class SelectStatement {
	private String[] mFields = new String[] { "*" };
	private String mFrom;
	private Where mWhere;
	private OrderBy mOrderBy;
	private Limit mLimit;
	private boolean mDistinct = false;
	private boolean mCount = false;
	
	public SelectStatement select(String... fields) {
		mFields = fields;
		
		return this;
	}
	
	public SelectStatement distinct() {
		mDistinct = true;
		
		return this;
	}
	
	public SelectStatement from(String table) {
		mFrom = table;
		
		return this;
	}
	
	public SelectStatement from(JoinStatement join) {
		mFrom = join.toString();
		
		return this;
	}
	
	public SelectStatement where(Where where) {
		mWhere = where;
		
		return this;
	}
	
	public SelectStatement orderBy(String col) {
		mOrderBy = new OrderBy(col);
		
		return this;
	}
	
	public SelectStatement limit(Limit limit) {
		mLimit = limit;
		
		return this;
	}
	
	public SelectStatement count() {
		mCount = true;
		
		return this;
	}
	
	private String buildDistinct() {
		if(mDistinct) {
			return " DISTINCT";
		}
		
		return "";
	}
	
	private String buildSelect() {
		if(mCount) {
			return " COUNT(*) AS count";
		}
		
		boolean first = true;
		
		String fields = " ";
		
		for(int i = 0, length = mFields.length; i < length; i++) {
			if(first) {
				fields += mFields[i];
				first = false;
			} else {
				fields += ", " + mFields[i];
			}
		}
		
		return fields;
	}
	
	private String buildWhere() {
		if(mWhere != null) {
			return mWhere.toString();
		}
		
		return "";
	}
	
	private String buildOrderBy() {
		if(mOrderBy != null) {
			return mOrderBy.toString();
		}
		
		return "";
	}
	
	private String buildLimit() {
		if(mLimit != null) {
			return mLimit.toString();
		}
		
		return "";
	}
	
	private String build() {
		return "SELECT"
			+ buildDistinct()
			+ buildSelect()
			+ " FROM " + mFrom
			+ buildWhere()
			+ buildOrderBy()
			+ buildLimit();
	}
	
	public String toString() {
		return build();
	}
}
