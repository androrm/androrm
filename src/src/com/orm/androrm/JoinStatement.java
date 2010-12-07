package com.orm.androrm;


public class JoinStatement {
	private SelectStatement mLeft;
	private SelectStatement mRight;
	private String mLeftAlias;
	private String mRightAlias;
	private String mLeftColumn;
	private String mRightColumn;

	public JoinStatement left(SelectStatement left, String as) {
		mLeft = left;
		mLeftAlias = as;
		
		return this;
	}
	
	public JoinStatement left(String tableName, String as) {
		SelectStatement select = new SelectStatement();
		select.from(tableName);
		
		return left(select, as);
	}
	
	public JoinStatement right(SelectStatement right, String as) {
		mRight = right;
		mRightAlias = as;
		
		return this;
	}
	
	public JoinStatement right(String tableName, String as) {
		SelectStatement select = new SelectStatement();
		select.from(tableName);
		
		return right(select, as);
	}
	
	public JoinStatement on(String leftColum, String rightColumn) {
		mLeftColumn = leftColum;
		mRightColumn = rightColumn;
		
		return this;
	}
	
	private String buildStatement(int counter) {
		
		String join = "(" +
				mLeft.toString() + 
			") AS " + mLeftAlias + 
			" JOIN (" +
				mRight.toString() +
			") AS " + mRightAlias +
			" ON " + 
				mLeftAlias + "." + mLeftColumn + 
				"=" +
				mRightAlias + "." + mRightColumn;
		
		return join;
				
	}
	
	public String toString() {
		return buildStatement(0);
	}
}
