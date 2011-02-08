/**
 * 
 */
package com.orm.androrm;

/**
 * @author Philipp Giese
 */
public class DeleteStatement implements Query {

	private String mFrom;
	private Where mWhere;

	public DeleteStatement from(String table) {
		mFrom = table;
		
		return this;
	}
	
	public DeleteStatement where(Where where) {
		mWhere = where;
		
		return this;
	}
	
	@Override
	public String toString() {
		return "DELETE FROM "
			+ mFrom
			+ mWhere;
	}
	
}
