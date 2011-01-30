/**
 * 	Copyright (c) 2010 Philipp Giese
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
	 * Retrieves a list of all referenced models. 
	 * 
	 * @param context	{@link Context} of the application.
	 * @param origin	Instance of the class, that is referencing.
	 * 
	 * @return {@link List} of referenced model classes. 
	 */
	public QuerySet<T> get(Context context, O origin);
	
	public List<T> getCachedValues();
}
