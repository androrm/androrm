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

import android.content.Context;

/**
 * This field implicitly assumes Foreign Key relations
 * from the target class to the class implementing
 * the One To Many Field. This way you can walk this
 * relations backwards. 
 * 
 * @author Philipp Giese
 */
public class OneToManyField<L extends Model, 
							R extends Model> 
extends AbstractToManyRelation<L, R> {

	public OneToManyField(Class<L> origin, Class<R> target) {
		mOriginClass = origin;
		mTargetClass = target;
		mValues = new ArrayList<R>();
	}
	
	@Override
	public QuerySet<R> get(Context context, L origin) {
		String fieldName = Model.getBackLinkFieldName(mTargetClass, mOriginClass);
		
		Filter filter = new Filter();
		filter.is(fieldName, origin);
		
		QuerySet<R> querySet = new QuerySet<R>(context, mTargetClass);
		querySet.filter(filter);
		
		return querySet;
	}
}
