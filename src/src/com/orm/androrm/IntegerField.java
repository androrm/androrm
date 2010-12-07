package com.orm.androrm;


public class IntegerField extends DataField<Integer> {

	public IntegerField() {
		mType = "integer";
		mMaxLength = 16;
	}
	
	public IntegerField(int maxLength) {
		mType = "integer";
		mMaxLength = 16;
		
		if(maxLength > 0
				&& maxLength <= 16) {
			
			mMaxLength = maxLength;
		}
	}
	
}
