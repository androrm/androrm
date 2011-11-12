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

import java.util.Collection;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * This class provides access to the underlying SQLite database. 
 * 
 * @author Philipp Giese
 */
public class DatabaseAdapter {
	
	/**
	 * Name that will be used for the database. Defaults
	 * to "my_database".
	 */
	private static String DATABASE_NAME = "my_database";
	
	/**
	 * Set the name, that will be used for the database.
	 * 
	 * @param name	Name of the database.
	 */
	public static final void setDatabaseName(String name) {
		DATABASE_NAME = name;
	}
	
	public static final String getDatabaseName() {
		return DATABASE_NAME;
	}
	
	/**
	 * {@link DatabaseAdapter.DatabaseHelper Database Helper} to deal with connecting to a SQLite database
	 * and creating tables.
	 */
	private DatabaseHelper mDbHelper;
	/**
	 * {@link android.database.sqlite.SQLiteDatabase SQLite database} to store the data.
	 */
	private SQLiteDatabase mDb;	
	
	public DatabaseAdapter(Context context) {
		mDbHelper = new DatabaseHelper(context, DATABASE_NAME);
	}
	
	/**
	 * Closes the current connection to the database.
	 * Call this method after every database interaction to prevent
	 * data leaks.
	 */
	public void close() {
		mDbHelper.close();
	}
	
	/**
	 * Delete one object or a set of objects from a specific table.
	 * 
	 * @param 	table 	Query table.
	 * @param 	where	{@link Where} clause to find the object.
	 * @return	Number of affected rows.
	 */
	public int delete(String table, Where where) {
		open();	
		int affectedRows = mDb.delete(table, where.toString().replace(" WHERE ", ""), null);
		close();
		
		return affectedRows;
	}
	
	/**
	 * Inserts values into a table that has an unique id as identifier.
	 * 
	 * @param 	table		The affected table.
	 * @param 	values		The values to be inserted/ updated.
	 * @param 	mId			The identifier of the affected row.
	 * 
	 * @return 	The number of rows affected on update, the rowId on insert, -1 on error.		
	 */
	public int doInsertOrUpdate(String table, ContentValues values, Where where) {
		int result;
		
		open();
		Cursor oldVersion = get(table, where, null);
		
		if(oldVersion.moveToNext()) {	
			String whereClause = null;
			if(where != null) {
				whereClause = where.toString().replace(" WHERE ", "");
			}
			
			result = mDb.update(table, values, whereClause, null);
		} else {	
			result = (int) mDb.insert(table, null, values);
		}
		
		oldVersion.close();
		close();
		return result;
	}
	
	/**
	 * Drops all tables of the current database. 
	 */
	public void drop() {
		open();
		
		mDbHelper.drop(mDb);		
		mDbHelper.onCreate(mDb);
		
		close();
	}
	
	/**
	 * Drops a specific table
	 * 
	 * @param 	tableName	Name of the table to drop.
	 */
	public void drop(String tableName) {
		open();
		
		String sql = "DROP TABLE IF EXISTS " + tableName + ";";
		mDb.execSQL(sql);
		mDbHelper.onCreate(mDb);
		
		close();
	}
	
	/**
	 * Query the database for a specific item.
	 * 
	 * @param 	table	Query table.
	 * @param 	where	{@link Where} clause to apply.
	 * @param 	limit	{@link Limit} clause to apply.
	 * @return	{@link Cursor} that represents the query result.
	 */
	private Cursor get(String table, Where where, Limit limit) {
		String whereClause = null;
		if(where != null) {
			whereClause = where.toString().replace(" WHERE ", "");
		} 
		
		String limitClause = null;
		if(limit != null) {
			limitClause = limit.toString().replace(" LIMIT ", "");
		}
		
		Cursor result = mDb.query(table, 
				null, 
				whereClause, 
				null, 
				null, 
				null, 
				null, 
				limitClause);
		
		return result;
	}
	
	/**
	 * This opens a new database connection. If a connection or database already exists
	 * the system will ensure that getWritableDatabase() will return this Database.
	 * 
	 * DO NOT try to do caching by yourself because this could result in an
	 * inappropriate state of the database.
	 * 
	 * @return this to enable chaining.
	 * @throws SQLException
	 */
	public DatabaseAdapter open() throws SQLException {
		mDb = mDbHelper.getWritableDatabase();
		
		return this;
	}
	
	public Cursor query(SelectStatement select) {
		return mDb.rawQuery(select.toString(), null);
	}
	
	/**
	 * Registers all models, that will then be handled by the
	 * ORM. 
	 * 
	 * @param models	{@link List} of classes inheriting from {@link Model}.
	 */
	public void setModels(Collection<Class<? extends Model>> models) {
		open();
		
		mDbHelper.setModels(mDb, models);
		
		close();
	}
}
