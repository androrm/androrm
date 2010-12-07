package com.orm.androrm;


/**
 * @author Philipp Giese
 *
 */
public class DoubleField extends DataField<Double> {

	public DoubleField() {
		mType = "numeric";
		mMaxLength = 16;
	}
	
	public DoubleField(int maxLength) {
		mType = "numeric";
		
		if(maxLength > 0
				&& maxLength <= 16) {
			
			mMaxLength = maxLength;
		}
	}
	
}
