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

/**
 * @author Philipp Giese
 */
public abstract class AbstractToManyRelation<O extends Model, 
											 T extends Model> 
implements XToManyRelation<O, T> {
	
	protected List<T> mValues;
	protected Class<O> mOriginClass;
	protected Class<T> mTargetClass;
	
	@Override
	public void add(T value) {
		if(value != null) {
			mValues.add(value);
		}
	}
	
	@Override
	public void addAll(Collection<T> values) {
		if(values != null) {
			mValues.addAll(values);
		}
	}
	
	@Override
	public Class<T> getTarget() {
		return mTargetClass;
	}

	@Override
	public void reset() {
		mValues.clear();
	}
	
	@Override
	public List<T> getCachedValues() {
		return mValues;
	}
}
