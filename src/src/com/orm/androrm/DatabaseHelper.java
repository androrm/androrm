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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Class to open up a database Connection. 
 * @author Philipp Giese
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String TAG = "ANDRORM:DB:HELPER";
	/**
	 * Version of the database.
	 */
	private static final int DATABASE_VERSION = 1;
	
	private static Set<String> mTables;
	private static Set<Class<? extends Model>> mModels;
	
	private static final Set<String> getTables() {
		if(mTables == null) {
			mTables = new HashSet<String>();
		}
		
		return mTables;
	}
	
	private static final Set<Class<? extends Model>> getModels() {
		if(mModels == null) {
			mModels = new HashSet<Class<? extends Model>>();
		}
		
		return mModels;
	}

	public DatabaseHelper(Context context, String dbName) {
		super(context, dbName, null, DATABASE_VERSION);
	}
	
	protected void setModels(SQLiteDatabase db, List<Class<? extends Model>> models) {
		mModels = new HashSet<Class<? extends Model>>();
		mModels.addAll(models);
		
		onCreate(db);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		for(Class<? extends Model> model: getModels()) {
			List<TableDefinition> tableDefinitions = Model.getTableDefinitions(model);
			
			for(TableDefinition definition: tableDefinitions) {
				db.execSQL(definition.toString());
				getTables().add(definition.getTableName());
			}
		}
	}

	/**
	 * Drops all tables of the database.
	 * @param db {@link SQLiteDatabase}.
	 */
	protected void drop(SQLiteDatabase db) {
		for(String table: getTables()) {
			db.execSQL("DROP TABLE IF EXISTS " + table);
		}
		
		mTables.clear();
		mModels.clear();
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to " +
				newVersion + ", which will destroy all data.");
		
		drop(db);
		onCreate(db);
		
	}
}
