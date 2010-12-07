/**
 * 
 */
package com.orm.androrm;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is the superclass of all statements. The whole statement
 * structure is used to build up where complex {@link Where} clauses. The 
 * abstract grammar of a {@link Statement} is the following.
 * 
 * <pre>
 *   KEY   <b>-></b> STRING
 *   VALUE <b>-></b> STRING
 *   
 *   STMT <b>-></b> KEY <b>= '</b>VALUE<b>'</b>
 *   STMT <b>-></b> LIKE_STMT
 *   STMT <b>-></b> COMPOSED_STMT
 *   
 *   LIKE_STMT <b>-></b> KEY <b>LIKE '%</b>VALUE<b>%'</b>
 *   
 *   COMPOSED_STMT <b>-></b> AND_STMT
 *   COMPOSED_STMT <b>-></b> OR_STMT
 *   
 *   AND_STMT <b>-></b> STMT
 *   AND_STMT <b>-></b> STMT <b>AND</b> STMT
 *   
 *   OR_STMT <b>-></b> STMT
 *   OR_STMT <b>-></b> STMT <b>OR</b> STMT
 * </pre>
 * 
 * @author Philipp Giese
 */
public class Statement {
	
	/**
	 * Key of the statement.
	 */
	protected String mKey;
	/**
	 * Value of the statement.
	 */
	protected String mValue;
	
	/**
	 * Empty constructor.
	 */
	public Statement() {}
	
	/**
	 * This constructor sets the key and value field of this 
	 * statement.
	 * 
	 * @param key Database column.
	 * @param value Expected value of this column.
	 */
	public Statement(String key, String value) {
		mKey = key;
		mValue = value;
	}
	
	public Statement(String key, int value) {
		mKey = key;
		mValue = String.valueOf(value);
	}
	
	public String toString() {
		return mKey + " = '" + mValue + "'";
	}
	
	/**
	 * Get all keys that are used in this statement.
	 * 
	 * @return All keys i.e. the affected database columns.
	 */
	public Set<String> getKeys() {
		Set<String> keys = new HashSet<String>();
		keys.add(mKey);
		
		return keys;
	}
}
