package com.orm.androrm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Class to open up a database Connection. 
 * When created it creates all tables needed by the Overture activity
 * if they do not exist already.
 * @author Philipp Giese
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String TAG = "ANDRORM:DB:HELPER";
	/**
	 * Version of the database.
	 */
	private static final int DATABASE_VERSION = 1;
	
	private List<String> mTables = new ArrayList<String>();
	private Set<Class<? extends Model>> mModels;

	public DatabaseHelper(Context context, String dbName) {
		super(context, dbName, null, DATABASE_VERSION);
		mModels = new HashSet<Class<? extends Model>>();
		mTables = new ArrayList<String>();
	}
	
	public <T extends Model> DatabaseHelper addModel(Class<T> model) {
		mModels.add(model);
		
		return this;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		for(Class<? extends Model> model: mModels) {
			List<TableDefinition> tableDefinitions = Model.getTableDefinitions(model);
			
			for(TableDefinition definition: tableDefinitions) {
				db.execSQL(definition.toString());
				mTables.add(definition.getTableName());
			}
		}
	}

	/**
	 * Drops all tables of the database.
	 * @param db {@link SQLiteDatabase}.
	 */
	public void drop(SQLiteDatabase db) {
		for(String table: mTables) {
			db.execSQL("DROP TABLE IF EXISTS " + table + ";");
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to " +
				newVersion + ", which will destroy all data.");
		
		drop(db);
		onCreate(db);
		
	}
}
