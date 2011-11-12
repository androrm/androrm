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

import android.util.Log;

/**
 * By utilizing this class you can build complex select statements. 
 * If no fields are given, SELECT * will be assumed.
 * 
 * @author Philipp Giese
 */
public class SelectStatement implements Cloneable {
	
	private static final String TAG = "ANDRORM:SELECT";
	
	private String[] mFields = new String[] { "*" };
	private String mFrom;
	private Where mWhere;
	private OrderBy mOrderBy;
	private Limit mLimit;
	private boolean mDistinct = false;
	private boolean mCount = false;
	
	private String buildDistinct() {
		if(mDistinct) {
			return " DISTINCT";
		}
		
		return "";
	}
	
	private String buildLimit() {
		if(mLimit != null) {
			return mLimit.toString();
		}
		
		return "";
	}
	
	private String buildOrderBy() {
		if(mOrderBy != null) {
			return mOrderBy.toString();
		}
		
		return "";
	}
	
	private String buildSelect() {
		if(mCount) {
			return " COUNT(*) AS " + Model.COUNT;
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
	
	/**
	 * Set this select to only return the count of the results. 
	 * <br /><br />
	 * This call is optional. 
	 * 
	 * @return
	 */
	public SelectStatement count() {
		mCount = true;
		
		return this;
	}
	
	/**
	 * Call this function, if the select shall be 
	 * distinct. 
	 * <br /><br />
	 * This call is optional. 
	 * 
	 * @return
	 */
	public SelectStatement distinct() {
		mDistinct = true;
		
		return this;
	}
	
	/**
	 * See {@link SelectStatement#from(String)}.
	 * <br /><br />
	 * 
	 * The only difference is, that here values will
	 * be selected from a subquery. 
	 * 
	 * @param join 	The {@link JoinStatement Join} from which
	 * 				the values will be selected.
	 * @return
	 */
	public SelectStatement from(JoinStatement join) {
		mFrom = join.toString();
		
		return this;
	}
	
	/**
	 * Specify the table from which the fields 
	 * shall be selected. 
	 * <br /><br />
	 * This call is <b>NOT</b> optional. 
	 * 
	 * @param table	Name of the table.
	 * @return
	 */
	public SelectStatement from(String table) {
		mFrom = table;
		
		return this;
	}
	
	public SelectStatement from(SelectStatement select) {
		mFrom = "(" + select.toString() + ")";
		
		return this;
	}
	
	/**
	 * {@link Limit} the results of the select.
	 * <br /><br />
	 * This call is optional. 
	 * 
	 * @param limit	{@link Limit} clause.
	 * @return
	 */
	public SelectStatement limit(Limit limit) {
		mLimit = limit;
		
		return this;
	}
	
	/**
	 * Define an ordering for the select. This function
	 * utilizes the {@link OrderBy} class. 
	 * <br /><br />
	 * This call is optional.
	 * 
	 * @param columns	All columns for the order by.
	 * 
	 * @return
	 */
	public SelectStatement orderBy(String... columns) {
		mOrderBy = new OrderBy(columns);
		
		return this;
	}
	
	public SelectStatement orderBy(OrderBy ordering) {
		mOrderBy = ordering;
		
		return this;
	}
	
	/**
	 * Hand in all fields, that shall be selected.
	 * If no fields are specified * will be 
	 * assumed. 
	 * <br /><br />
	 * This call is optional. 
	 * 
	 * @param fields	Names of fields, that will be selected. 
	 * 
	 * @return
	 */
	public SelectStatement select(String... fields) {
		mFields = fields;
		
		return this;
	}
	
	@Override
	public String toString() {
		return "SELECT"
			+ buildDistinct()
			+ buildSelect()
			+ " FROM " + mFrom
			+ buildWhere()
			+ buildOrderBy()
			+ buildLimit();
	}
	
	/**
	 * Hand in a {@link Where} statement to drill down 
	 * the results of the select. 
	 * <br /><br />
	 * This call is optional.
	 * 
	 * @param where	{@link Where} clause.
	 * @return
	 */
	public SelectStatement where(Where where) {
		mWhere = where;
		
		return this;
	}
	
	public Where getWhere() {
		return mWhere;
	}
	
	@Override
	public SelectStatement clone() {
		try {
			return (SelectStatement) super.clone();
		} catch(CloneNotSupportedException e) {
			Log.e(TAG, "could not clone object", e);
		}
		
		return null;
	}
}
