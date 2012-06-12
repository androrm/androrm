/**
 * 	Copyright (c) 2012 Philipp Giese
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
package com.orm.androrm.migration;

import android.content.Context;

import com.orm.androrm.DatabaseBuilder;
import com.orm.androrm.Model;
import com.orm.androrm.QuerySet;
import com.orm.androrm.field.CharField;

public class Migration extends Model {
	
	public static final<T extends Model> Migration create(Class<T> model, Migratable<T> migration) {
		Migration self = new Migration();
		
		self.mModel.set(DatabaseBuilder.getTableName(model));
		self.mAction.set(migration.getAction());
		self.mValue.set(migration.getValue(model));
		
		return self;
	}
	
	public static QuerySet<Migration> objects(Context context) {
		return objects(context, Migration.class);
	}

	protected CharField mModel;
	protected CharField mAction;
	protected CharField mValue;
	
	public Migration() {
		super();
		
		mModel = new CharField();
		mAction = new CharField();
		mValue = new CharField();
	}
	
}
