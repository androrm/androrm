package com.orm.androrm;


public class SelectStatement {
	private String[] mFields = new String[] { "*" };
	private String mFrom;
	private Where mWhere;
	private Limit mLimit;
	private boolean mDistinct = false;
	
	public SelectStatement select(String[] fields) {
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
		if(mWhere == null) {
			mWhere = new Where();
		}
		
		mWhere.orderBy(col);
		
		return this;
	}
	
	public SelectStatement limit(Limit limit) {
		mLimit = limit;
		
		return this;
	}
	
	private String buildDistinct() {
		if(mDistinct) {
			return " DISTINCT";
		}
		
		return "";
	}
	
	private String buildSelect() {
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
			String where = mWhere.toString();
			
			if(where != null) {
				return " WHERE " + where;
			}
		}
		
		return "";
	}
	
	private String buildOrderBy() {
		if(mWhere != null) {
			String orderBy = mWhere.getOrderBy();
			
			if(orderBy != null) {
				return " ORDER BY " + orderBy;
			}
		}
		
		return "";
	}
	
	private String buildLimit() {
		if(mLimit != null) {
			return " LIMIT " + mLimit.toString();
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
