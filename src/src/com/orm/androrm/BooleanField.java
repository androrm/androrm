package com.orm.androrm;


/**
 * @author Philipp Giese
 *
 */
public class BooleanField extends DataField<Boolean>{

	public BooleanField() {
		mType = "integer";
		mValue = false;
		mMaxLength = 1;
	}
	
	@Override
	public String getDefinition(String fieldName) {
		return fieldName 
			+ " " 
			+ mType 
			+ "(" 
			+ mMaxLength 
			+ ")";
	}

}