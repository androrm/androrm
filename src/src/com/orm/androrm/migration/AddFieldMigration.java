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
import com.orm.androrm.DatabaseField;
import com.orm.androrm.Model;

public class AddFieldMigration extends AndrormMigration {

	DatabaseField<?> mFieldInstance;
	
	public AddFieldMigration(String name, DatabaseField<?> field) {
		super(name);

		mFieldInstance = field;
		
		ACTION = "add_field";
	}

	@Override
	public boolean execute(Class<? extends Model> model, Context context) {
		String sql = "ALTER TABLE `" + DatabaseBuilder.getTableName(model) + "` " +
				"ADD COLUMN " + mFieldInstance.getDefinition(mValue);
		
		return exec(context, model, sql);
	}

}
