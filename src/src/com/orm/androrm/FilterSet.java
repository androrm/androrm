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
import java.util.List;

/**
 * @author Philipp Giese
 *
 */
public class FilterSet {
	private List<Filter> mFilters;
	
	public FilterSet() {
		mFilters = new ArrayList<Filter>();
	}
	
	private String getFieldName(String sequence) {
		String[] fields = sequence.split("__");
		
		return fields[fields.length - 1];
	}
	
	public FilterSet in(String key, List<?> values) {
		List<Integer> filteredValues = new ArrayList<Integer>();		
		for(Object value: values) {
			if(value instanceof Integer) {
				filteredValues.add((Integer) value);
			} else if(value instanceof Model) {
				Model m = (Model) value;
				filteredValues.add(m.getId());
			}
		}
		
		mFilters.add(new Filter(key, new InStatement(getFieldName(key), filteredValues)));
		
		return this;
	}
	
	public FilterSet contains(String key, String needle) {
		mFilters.add(new Filter(key, new LikeStatement(getFieldName(key), needle)));
		
		return this;
	}
	
	public FilterSet is(String key, String value) {
		mFilters.add(new Filter(key, new Statement(getFieldName(key), value)));
		
		return this;
	}
	
	public FilterSet is(String key, Integer value) {
		return is(key, String.valueOf(value));
	}
	
	public FilterSet is(String key, Model value) {
		return is(key, value.getId());
	}
	
	public List<Filter> getFilters() {
		return mFilters;
	}
}
