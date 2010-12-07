/**
 * 
 */
package com.orm.androrm;

/**
 * @author Philipp Giese
 *
 */
public interface Relation {
	public Class<? extends Model> getTarget();
}
