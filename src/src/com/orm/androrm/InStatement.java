/**
 * 
 */
package com.orm.androrm;

import java.util.List;

/**
 * @author Philipp Giese
 *
 */
public class InStatement extends Statement {

	private List<Integer> mValues;
	
	public InStatement(String key, List<Integer> values) {
		mKey = key;
		mValues = values;
	}
	
	@Override
	public String toString() {
		String stmt = mKey + " IN (";
		boolean first = true;
		
		for(Integer value: mValues) {
			if(first) {
				stmt += value;
				first = false;
			} else {
				stmt += "," + value;
			}
		}
		
		stmt += ")";
		
		return stmt;
	}
}
