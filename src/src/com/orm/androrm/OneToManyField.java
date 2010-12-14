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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;

/**
 * @author Philipp Giese
 *
 */
public class OneToManyField<L extends Model, R extends Model> implements XToManyRelation<L> {

	private Set<R> mValues;
	private Class<R> mTargetClass;
	private Class<L> mOriginClass;
	
	public OneToManyField(Class<L> origin, Class<R> target) {
		mOriginClass = origin;
		mTargetClass = target;
		mValues = new HashSet<R>();
	}
	
	@Override
	public Class<? extends Model> getTarget() {
		return mTargetClass;
	}
	
	public void add(L origin, R value) {
		mValues.add(value);
	}
	
	public void addAll(L origin, Collection<R> values) {
		for(R value: values) {
			add(origin, value);
		}
	}
	
	public List<R> get(Context context, L l, Limit limit) {
		if(mValues.isEmpty()) {
			String fieldName = Model.getBackLinkFieldName(mTargetClass, mOriginClass);
			
			FilterSet filter = new FilterSet();
			filter.is(fieldName, l);
			
			List<R> result = Model.filter(context, mTargetClass, filter);
			
			mValues.addAll(result);
		}
		
		return new ArrayList<R>(mValues);
	}

	public List<R> get(Context context, L l) {
		return get(context, l, null);
	}

	@Override
	public int count(Context context, L l) {
		if(l.getId() != 0) {
			String fieldName = Model.getBackLinkFieldName(mTargetClass, mOriginClass);
			
			FilterSet filter = new FilterSet();
			filter.is(fieldName, l);
			
			return Model.count(context, mTargetClass, filter);
		}
		
		/*
		 * even though the relation is not persisted objects
		 * could have been added to via the add method. In this 
		 * case the result of count is the size of the mValues
		 * list.
		 */
		return mValues.size();
	}
}
