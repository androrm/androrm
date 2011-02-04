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

import android.content.ContentValues;
import android.database.Cursor;

/**
 * This interface has to be implemented by all classes, that will
 * represent fields of the underlying database. The type parameter
 * specifies the Java data type, the implementing class will
 * represent. 
 * 
 * @author Philipp Giese
 *
 * @param <T>	Java data type of the field.
 */
public interface DatabaseField<T> extends AndrormField {
	/**
	 * Used to gather the current value of this field.
	 * @return The value of the field.
	 */
	public T get();
	
	/**
	 * Creates the database specific definition for this
	 * type of field.
	 * 
	 * @param fieldName	Name of that field, that shall be used for the definition.
	 * @return The definition used to create the field in the database.
	 */
	public String getDefinition(String fieldName);
	
	/**
	 * When this field is serialized into the database this method is
	 * called. The field can then put it's data into the {@link ContentValues}
	 * variable under the given fieldName. 
	 *  
	 * @param fieldName	Key for the {@link ContentValues} variable and also name
	 * 					of this field in the object, that is being serialized. 
	 * @param values	{@link ContentValues} object holding all data, that will
	 * 					be written to the database.
	 */
	public void putData(String fieldName, ContentValues values);
	
	/**
	 * This method is used to read the value in the right 
	 * format out of a {@link Cursor}. As only the implementing
	 * field knows the correct data type this responsibility is
	 * hence handed to it. 
	 *  
	 * @param c				{@link Cursor} pointing at data.
	 * @param columnIndex	Index of the field value in the cursor. 
	 */
	public void set(Cursor c, String fieldName);
	
	/**
	 * Sets the value of this field. 
	 * 
	 * @param value	The value this field is set to.
	 */
	public void set(T value);
}
