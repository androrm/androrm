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
 * Filters are a mechanism, to execute complex 
 * queries on the model classes in a very easy manner
 * by using the field names in these classes. 
 * <br /><br />
 * 
 * <b>Example:</b><br />
 * If you for example want all Suppliers, that have a
 * Branch which's name contains Pretoria the {@link Filter}
 * would be the following. 
 * <br /><br />
 * 
 * <pre>
 * Filter filter = new Filter();
 * filter.contains("mBranches__mName", "Pretoria");
 * </pre>
 * 
 * @author Philipp Giese
 */
public class Filter {
	/**
	 * {@link List} of all {@link Rule filters}, that were
	 * added to this set.
	 */
	private List<Rule> mRules;
	
	public Filter() {
		mRules = new ArrayList<Rule>();
	}
	
	/**
	 * Use this function, if you want to express, that the value
	 * of the field should contain the given string. Note, that this
	 * function is <b>NOT</b> case sensitive. 
	 * 
	 * @param key		Chain leading to the field.
	 * @param needle	String that shall be contained. 
	 * @return <code>this</code> for chaining.
	 */
	public Filter contains(String key, String needle) {
		mRules.add(new Rule(key, new LikeStatement(getFieldName(key), needle)));
		
		return this;
	}
	
	private List<Integer> filterValues(List<?> values) {
		List<Integer> filteredValues = new ArrayList<Integer>();		
		for(Object value: values) {
			if(value instanceof Integer) {
				filteredValues.add((Integer) value);
			} else if(value instanceof Model) {
				Model m = (Model) value;
				filteredValues.add(m.getId());
			}
		}
		
		return filteredValues;
	}
	
	/**
	 * Retrieves the last field in the chain, 
	 * to gather the correct field name for the 
	 * statement. 
	 * 
	 * @param sequence	List of field names separated by __
	 * @return The last field name in the chain.
	 */
	private String getFieldName(String sequence) {
		String[] fields = sequence.split("__");
		
		return fields[fields.length - 1];
	}
	
	public List<Rule> getRules() {
		return mRules;
	}
	
	/**
	 * Use this function, if you want the value of the field
	 * to be in the {@link List} of values you hand in. 
	 * <br /><br />
	 * 
	 * This can either be a list of {@link Integer integers} or 
	 * a list of model classes. (Classes inheriting from {@link Model})
	 * 
	 * @param key		Chain to the field.
	 * @param values	{@link List} of values.
	 * @return	<code>this</code> for chaining.
	 */
	public Filter in(String key, List<?> values) {
		mRules.add(new Rule(key, new InStatement(getFieldName(key), filterValues(values))));
		
		return this;
	}
	
	/**
	 * See {@link Filter#is(String, String)}.
	 */
	public Filter is(String key, Integer value) {
		return is(key, String.valueOf(value));
	}
	
	/**
	 * See {@link Filter#is(String, String)}.
	 */
	public Filter is(String key, Model value) {
		return is(key, value.getId());
	}
	
	/**
	 * Use this function, if you want to express, that the value
	 * of the field should be <b>exactly</b> the given value. 
	 * 
	 * @param key	Chain leading to the field.
	 * @param value	Desired value of the field.
	 * @return <code>this</code> for chaining.
	 */
	public Filter is(String key, String value) {
		mRules.add(new Rule(key, new Statement(getFieldName(key), value)));
		
		return this;
	}
}
