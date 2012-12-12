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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.orm.androrm.migration.Migration;
import com.orm.androrm.statement.SelectStatement;

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
	
	private static DatabaseAdapter mInstance;
	
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
	
	public static final DatabaseAdapter getInstance(Context context) {
		if(mInstance == null) {
			mInstance = new DatabaseAdapter(context);
		}
		
		return mInstance;
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
	
	private Context mContext;
	
	private int mRunningTransactions;
	
	/**
	 * This constructor is deprecated. In order to retrieve an instance
	 * call {@link DatabaseAdapter#getInstance(Context)}.
	 * 
	 * @param context {@link Context} of the application.
	 */
	@Deprecated
	public DatabaseAdapter(Context context) {
		mDbHelper = new DatabaseHelper(context, DATABASE_NAME);
		mContext = context;
		mRunningTransactions = 0;
	}
	
	/**
	 * Closes the current connection to the database.
	 * Call this method after every database interaction to prevent
	 * data leaks.
	 */
	public void close() {
		if(mRunningTransactions == 0) {
			mDbHelper.close();
		}
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
		
		if(oldVersion.moveToNext() && values.size() != 0) {	
			String whereClause = null;
			if(where != null) {
				whereClause = where.toString().replace(" WHERE ", "");
			}

			result = mDb.update(table, values, whereClause, null);
		} else {	
			String nullColumnHack = null;
			
			if(values.size() == 0) {
				// if no fields are defined on a model instance the nullColumnHack
				// needs to be utilized in order to insert an empty row. 
				nullColumnHack = Model.PK;
			}
			
			result = (int) mDb.insertOrThrow(table, nullColumnHack, values);
		}
		
		oldVersion.close();
		close();
		return result;
	}
	
	public void reloadSchema() {
		open();
		
		mDbHelper.onCreate(mDb);
		
		close();
	}
	
	/**
	 * This method will not actually rename an existing table as 
	 * it is only called after the initialization phase is over.
	 * Thus the new table already exists in the system. Renaming
	 * now means filling the new table with the data of the old
	 * one and afterwards removing the old table. 
	 * 
	 * @param from	Current name of the table.
	 * @param to	Desired name of the table. 
	 */
	public void renameTable(String from, String to) {
		open();
		
		try {
			mDbHelper.renameTable(mDb, from, to);
			
			drop(from);
		} catch(SQLException e) {
			close();
			
			throw e;
		}
		
		close();
	}
	
	/**
	 * Drops all tables of the current database. 
	 */
	public void drop() {
		open();
		
		mDbHelper.drop(mDb);		
		
		close();
		
		ModelCache.reset();
	}
	
	/**
	 * Drops a specific table
	 * 
	 * @param 	tableName	Name of the table to drop.
	 */
	public void drop(String tableName) {
		open();
		
		String sql = "DROP TABLE IF EXISTS `" + tableName + "`;";
		mDb.execSQL(sql);
		
		ModelCache.reset(tableName);
		
		close();
	}
	
	public void resetMigrations() {
		open();
		
		String sql = "DROP TABLE IF EXISTS `" + DatabaseBuilder.getTableName(Migration.class) + "`;";
		mDb.execSQL(sql);

		ModelCache.reset(DatabaseBuilder.getTableName(Migration.class));
		
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
	
	public void increaseTransactionCounter() {
		mRunningTransactions++;
	}
	
	public void decreaseTransactionCounter() {
		mRunningTransactions--;
	}
	
	public void resetTransactionCounter() {
		mRunningTransactions = 0;
	}
	
	public int runningTransactions() {
		return mRunningTransactions;
	}
	
	/**
	 * Starts a new transaction on the database. Note, that transaction
	 * can also be nested. So in order to work properly ALWAYS acquire a
	 * {@link DatabaseAdapter} instance via the {@link DatabaseAdapter#getInstance(Context)}
	 * method.
	 * 
	 * @return Current {@link DatabaseAdapter} instance.
	 */
	public DatabaseAdapter beginTransaction() {
		open();
		
		mDb.beginTransactionWithListener(TransactionListener.getFor(this));
		
		return this;
	}
	
	/**
	 * This method will set the current transaction as successful and end it
	 * afterwards, thus committing all the data. 
	 * @return
	 */
	public DatabaseAdapter commitTransaction() {
		mDb.setTransactionSuccessful();
		mDb.endTransaction();
		
		close();
		
		return this;
	}

	public DatabaseAdapter rollbackTransaction() {
		mDb.endTransaction();
		
		close();
		
		return this;
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
		if(mRunningTransactions == 0) {
			mDb = mDbHelper.getWritableDatabase();
		}
		
		return this;
	}
	
	public Cursor query(SelectStatement select) {
		return mDb.rawQuery(select.toString(), null);
	}
	
	public Cursor query(String query) {
		return mDb.rawQuery(query, null);
	}
	
	/**
	 * Execute a single SQL statement that is NOT a SELECT or any other SQL statement that returns data.
	 * 
	 * @param query
	 */
	public void exec(String query) {
		mDb.execSQL(query);
	}
	
	/**
	 * Registers all models, that will then be handled by the
	 * ORM. 
	 * 
	 * @param models	{@link List} of classes inheriting from {@link Model}.
	 */
	public void setModels(List<Class<? extends Model>> models) {
		open();
		
		mDbHelper.setModels(mDb, models);
		
		close();
		
		// After all tables have initially been created, run the migrations 
		// on them in order to get all of them up to date.
		Model.runMigrations(mContext, models);
	}
}
