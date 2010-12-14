/**
 * 
 */
package com.orm.androrm;

import android.content.Context;

/**
 * @author Philipp Giese
 *
 */
public interface XToManyRelation<T extends Model> extends Relation {
	public int count(Context context, T origin);
}
