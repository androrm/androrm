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

import java.util.List;

/**
 * This exception will be thrown if an attempt is made to 
 * access a field on a class, that is either not present
 * or not accessible from the {@link Model} class. Hence 
 * building a query would not produce a valuable outcome. 
 *   
 * @author Philipp Giese
 */
public class NoSuchFieldException extends RuntimeException {

	private static final long serialVersionUID = -2025468982439559222L;

	/**
	 * This constructor should be used, if this exception is being raised
	 * as it is the most verbose one. 
	 * 
	 * @param fieldName	The name of the field, that produced the error
	 * @param choices 	{@link List} of choices for fields, that are available
	 * 					See {@link Model#getEligibleFields(Class, Model)} for more
	 * 					information.
	 */
	public NoSuchFieldException(String fieldName, List<String> choices) {
		super("No such field: " + fieldName + "! Choices are: " + choices);
	}
	
	public NoSuchFieldException(String msg) {
		super(msg);
	}
}
