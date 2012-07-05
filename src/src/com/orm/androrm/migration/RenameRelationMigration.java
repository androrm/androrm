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

import java.util.List;

import android.content.Context;
import android.database.SQLException;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.Model;

/**
 * This migration will make sure that all relational tables, that belong
 * to a certain model are renamed to fit to the new name of the model. 
 * 
 * @author Philipp Giese
 *
 * @param <T>
 */
public class RenameRelationMigration<T extends Model> extends RenameModelMigration<T> {

	protected RenameRelationMigration(String oldName) {
		super(oldName);
	}

	@Override
	public boolean execute(Context context, Class<T> model) {
		if(isApplied(model, context)) {
			return false;
		}
		
		MigrationHelper helper = new MigrationHelper(context);
		
		List<String> tables = helper.getRelationTableNames(mOldName);
		String newName = getValue(model);
		
		DatabaseAdapter adapter = new DatabaseAdapter(context);
		
		for(String table : tables) {
			try {
				adapter.renameTable(table, table.replace(mOldName, newName));
			} catch(SQLException e) {
				return false;
			}
		}
		
		return true;
	}

}
