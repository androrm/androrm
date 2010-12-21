/**
 * 
 */
package com.orm.androrm;

import java.util.Collection;
import java.util.List;

import android.content.Context;

/**
 * This interface has to be implemented by all relations, that
 * point to many other instances. 
 * 
 * @author Philipp Giese
 */
public interface XToManyRelation<O extends Model, T extends Model> extends Relation {
	public int count(Context context, O origin);
	public List<T> get(Context context, O origin);
	public List<T> get(Context context, O origin, Limit limit);
	public void add(T value);
	public void addAll(Collection<T> values);
}
