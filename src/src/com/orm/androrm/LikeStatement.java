/**
 * 
 */
package com.orm.androrm;

/**
 * @author Philipp Giese
 */
public class LikeStatement extends Statement {

	public LikeStatement(String key, String value) {
		super(key, value);
	}
	
	@Override
	public String toString() {
		return mKey + " LIKE '%" + mValue + "%'";
	}
}
