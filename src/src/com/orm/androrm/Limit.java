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
 * Class representing the LIMIT statement of the database.
 * 
 * @author Philipp Giese
 */
public class Limit {
	/**
	 * Start offset.
	 */
	private int mOffset;
	/**
	 * Number of items to fetch.
	 */
	private int mLimit;
	
	public Limit() {
		mLimit = 0;
		mOffset = 0;
	}
	
	public Limit(int limit) {
		mLimit = limit;
		mOffset = 0;
	}
	
	public Limit(int offset, int limit) {
		mOffset = offset;
		mLimit = limit;
	}
	
	/**
	 * The computed limit is the offset plus the number of items, that
	 * shall be fetched. These numbers are handed to the database.
	 * 
	 * @return The computed limit for the query.
	 */
	public int getComputedLimit() {
		return mOffset + mLimit;
	}
	
	/**
	 * @return The offset to start.
	 */
	public int getOffset() {
		return mOffset;
	}
	
	/**
	 * The raw limit is simply the count of objects that shall be fetched.
	 * 
	 * @return The raw limit.
	 */
	public int getRawLimit() {
		return mLimit;
	}
	
	@Override
	public String toString() {
		if(mLimit != 0) {
		
			if(mOffset == 0) {
				return " LIMIT " + String.valueOf(mLimit);
			}
			
			return " LIMIT " + mOffset + " , " + (mOffset + mLimit);
		}
		
		return null;
	}
}
