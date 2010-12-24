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
 *
 * @param <O>	Origin type.
 * @param <T>	Target type.
 */
public interface XToManyRelation<O extends Model, T extends Model> extends Relation<T> {
	/**
	 * Adds a new value to the list of referenced models.
	 * 
	 * @param value	Model instance that shall be referenced. 
	 */
	public void add(T value);
	
	/**
	 * Adds all items of the {@link Collection} to the list
	 * of referenced models.
	 * 
	 * @param values {@link Collection} of models. 
	 */
	public void addAll(Collection<T> values);
	
	/**
	 * Get the number of referenced models.
	 * 
	 * @param context	{@link Context} of the application.
	 * @param origin	Instance of the class, that is referencing. 
	 * 
	 * @return Number of referenced models. 
	 */
	public int count(Context context, O origin);
	
	/**
	 * Retrieves a list of all referenced models. 
	 * 
	 * @param context	{@link Context} of the application.
	 * @param origin	Instance of the class, that is referencing.
	 * 
	 * @return {@link List} of referenced model classes. 
	 */
	public List<T> get(Context context, O origin);
	
	/**
	 * Same as {@link XToManyRelation#get(Context, Model)} only with a 
	 * {@link Limit} parameter to slice the result. 
	 * 
	 * @param context	{@link Context} of the application.
	 * @param origin	Instance of the class, that is referencing. 
	 * @param limit		{@link Limit} clause.
	 * 
	 * @return {@link List} of referenced model classes. 
	 */
	public List<T> get(Context context, O origin, Limit limit);
}
