package com.orm.androrm;


public class CharField extends DataField<String> {

	public CharField() {
		mType = "varchar";
		mMaxLength = 255;
	}
	
	public CharField(int maxLength) {
		mType = "varchar";
		mMaxLength = 255;
		
		if(maxLength > 0
				&& maxLength <= 255) {
			
			mMaxLength = maxLength;
		}
	}

}
