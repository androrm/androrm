/**
 * Overture is proprietary software of the SAP AG
 */
package com.orm.androrm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * This class provides access to the underlying SQLite database. The keys defined here
 * are to be used throughout the application for reasons of consistency.
 * 
 * @author Philipp Giese
 */
public class DatabaseAdapter {
	
	/**
	 * Tag that can be used for logging.
	 */
	private static final String TAG = "ANDRORM:ADAPTER";
	private static Set<Class<? extends Model>> mModels;
	public static final void setModels(List<Class<? extends Model>> models) {
		if(mModels == null) {
			mModels = new HashSet<Class<? extends Model>>();
		}
		
		mModels.addAll(models);
	}
	private static final Set<Class<? extends Model>> getModels() {
		if(mModels == null) {
			mModels = new HashSet<Class<? extends Model>>();
		}
		
		return mModels;
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
	/**
	 * Android {@link android.content.Context context} that the application is running in.
	 */
	
	public DatabaseAdapter(Context context) {
		mDbHelper = new DatabaseHelper(context, "catalogue");
		
		for(Class<? extends Model> model: getModels()) {
			mDbHelper.addModel(model);
		}
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
	
	/**
	 * Closes the current connection to the database.
	 * Call this method after every database interaction to prevent
	 * data leaks.
	 */
	public void close() {
		mDbHelper.close();
	}
	
	/**
	 * Drops all tables of the current database. 
	 */
	public void drop() {
		open();
		
		Log.d(TAG, "attempting to drop the database");
		
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
	
	public Cursor query(String sql) {
		return mDb.rawQuery(sql, null);
	}
	
	/**
	 * Query the database for a specific item.
	 * 
	 * @param 	table	Query table.
	 * @param 	where	{@link Where} clause to apply.
	 * @param 	limit	{@link Limit} clause to apply.
	 * @return	{@link Cursor} that represents the query result.
	 */
	public Cursor get(String table, Where where, Limit limit) {
		if(where == null) {
			where = new Where();
		}
		
		if(limit == null) {
			limit = new Limit();
		}
		
		Cursor result = mDb.query(table, null, where.toString(), null, null, null, null, limit.toString());
		
		return result;
	}
	
	/**
	 * Retrieves all IDs of the entries that match the where clause in the specified table
	 * The field in select will be inserted into the result ArrayList.
	 * 
	 * @param 	table	Table to query.
	 * @param 	select	AbstractField to select for result.
	 * @param 	where 	ContentValues object representing whereClause
	 * @return	A list of IDs of objects that match the query.
	 */
	public List<Integer> getAll(String table, String[] select, Where where, Limit limit) {
		open();
		
		if(where == null) {
			where = new Where();
		}
		
		if(limit == null) {
			limit = new Limit();
		}
		
		Log.d(TAG, "performing query:" +
				" SELECT " + select[0] + 
				" FROM " + table + 
				" WHERE " + where + 
				" LIMIT " + limit);
		
		Cursor result = mDb.query(true, table, select, where.toString(), null, null, null, where.getOrderBy(), limit.toString());
		List<Integer> IDs = new ArrayList<Integer>();
		
		while(result.moveToNext()) {
			int id = result.getInt(result.getColumnIndexOrThrow(select[0]));
			IDs.add(id);
		}
		
		result.close();
		close();
		return IDs;
	}
	
	/**
	 * Convenience method. Just places a default value for the select statement.
	 * For more information see {@link DatabaseAdapter#getAll(String, String[], ContentValues) }
	 * 
	 * @param 	table	Table to query.
	 * @param 	where 	{@link ContentValues} object representing whereClause.
	 * @param	limit	{@link ContentValues} object representing limitClause.
	 * @return	See {@link DatabaseAdapter#getAll(String, String[], ContentValues) }
	 */
	public List<Integer> getAll(String table, Where where, Limit limit) {
		return getAll(table, new String[] { "_id" }, where, limit);
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
			result = mDb.update(table, values, where.toString(), null);
		} else {	
			result = (int) mDb.insert(table, null, values);
		}
		
		oldVersion.close();
		close();
		return result;
	}
	
	public int doInsert(String table, ContentValues values) {
		int result; 
		
		open();
		result = (int) mDb.insert(table, null, values);
		close();
		
		return result;
	}
	
	/**
	 * Adds a relation between two tables where not an id serves as primary but the two
	 * foreign keys of the affected tables.
	 * 
	 * @param 	table		The table that holds the relation.
	 * @param 	values		The two values to be inserted.
	 * 
	 * @return	The rowId of the new row on success, -1 on error.
	 */
	public int addRelation(String table, ContentValues values) {
		open();
		int result = (int) mDb.insertOrThrow(table, null, values);
		close();
		
		return result;
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
		int result = mDb.delete(table, where.toString(), null);
		close();
		
		return result;
	}
}
