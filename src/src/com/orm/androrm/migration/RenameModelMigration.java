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

import java.util.Locale;

import android.content.Context;
import android.database.SQLException;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.DatabaseBuilder;
import com.orm.androrm.Model;

/**
 * This kind of migration is used to rename tables. If the name of a model
 * has change, also the corresponding table needs to be renamed in order 
 * for lookups to succeed. Furthermore we need to make sure, that all 
 * relational tables, that have been created are renamed. 
 * 
 * @author Philipp Giese
 *
 * @param <T>	Type of the model, that will be renamed. 
 */
public class RenameModelMigration<T extends Model> extends AndrormMigration<T> {

	protected String mOldName;
	
	public RenameModelMigration(String old) {
		super(null, "rename_table");
		
		mOldName = old.toLowerCase(Locale.getDefault());
	}
	
	public RenameModelMigration(String old, String action) {
		super(null, action);
		
		mOldName = old.toLowerCase(Locale.getDefault());
	}

	@Override
	public boolean execute(Context context, Class<T> model) {
		if(isApplied(model, context)) {
			return false;
		}
		
		MigrationHelper helper = new MigrationHelper(context);
		
		if(!helper.tableExists(mOldName)) {
			// if the table, that should be renamed doesn't exist anymore
			// we have to assume, that this migration is rolled out on
			// a newer version of the database. Thus we need to save, that
			// it has already been applied, but must not run the command
			// on the database.
			return true;
		}
		
		DatabaseAdapter adapter = DatabaseAdapter.getInstance(context);
		
		renameRelationTables(context, model);
		
		try {
			adapter.renameTable(mOldName, getValue(model));
		} catch (SQLException e) {
			return false;
		}
		
		return true;
	}
	
	private void renameRelationTables(Context context, Class<T> model) {
		MigrationHelper helper = new MigrationHelper(context);
		
		if(helper.hasRelationTable(mOldName)) {
			Migrator<T> migrator = new Migrator<T>(model);
			migrator.renameRelation(mOldName, model);
			migrator.migrate(context);
		}
	}

	@Override
	public String getValue(Class<T> model) {
		return DatabaseBuilder.getTableName(model);
	}

}
