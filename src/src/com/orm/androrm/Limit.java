/**
 * 
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
	
	public Limit(int offset, int limit) {
		this.mOffset = offset;
		this.mLimit = limit;
	}
	
	public Limit(int limit) {
		this.mLimit = limit;
		this.mOffset = 0;
	}
	
	public Limit() {
		this.mLimit = 0;
		this.mOffset = 0;
	}
	
	/**
	 * The computed limit is the offset plus the number of items, that
	 * shall be fetched. These numbers are handed to the database.
	 * 
	 * @return The computed limit for the query.
	 */
	public int getComputedLimit() {
		return this.mOffset + this.mLimit;
	}
	
	/**
	 * The raw limit is simply the count of objects that shall be fetched.
	 * 
	 * @return The raw limit.
	 */
	public int getRawLimit() {
		return this.mLimit;
	}
	
	/**
	 * @return The offset to start.
	 */
	public int getOffset() {
		return this.mOffset;
	}
	
	@Override
	public String toString() {
		if(mLimit != 0) {
		
			if(mOffset == 0) {
				return String.valueOf(mLimit);
			}
			
			return mOffset + " , " + (mOffset + mLimit);
		}
		
		return null;
	}
}
