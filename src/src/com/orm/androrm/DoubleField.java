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
 * @author Philipp Giese
 *
 */
public class DoubleField extends DataField<Double> {

	public DoubleField() {
		setUp();
	}
	
	public DoubleField(int maxLength) {
		setUp();
		
		if(maxLength > 0
				&& maxLength <= 16) {
			
			mMaxLength = maxLength;
		}
	}
	
	private void setUp() {
		mType = "numeric";
		mValue = 0.0;
	}

	@Override
	public void set(Cursor c, int columnIndex) {
		set(c.getDouble(columnIndex));
	}

	@Override
	public void putData(String key, ContentValues values) {
		values.put(key, get());
	}
	
}
