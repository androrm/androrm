package com.orm.androrm.migration;

import android.content.Context;
import android.database.Cursor;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.DatabaseBuilder;
import com.orm.androrm.Model;

public class MigrationHelper {

	private DatabaseAdapter mAdapter;
	
	public MigrationHelper(Context context) {
		mAdapter = new DatabaseAdapter(context);
	}
	
	private Cursor getCursor(String query) {
		mAdapter.open();
		return mAdapter.query(query);
	}
	
	private void close(Cursor cursor) {
		cursor.close();
		mAdapter.close();
	}
	
	public boolean hasField(Class<? extends Model> model, String name) {
		String table = DatabaseBuilder.getTableName(model);
		String sql = "PRAGMA TABLE_INFO(`" + table + "`)";
		
		Cursor c = getCursor(sql);
		
		while(c.moveToNext()) {
			String fieldName = c.getString(c.getColumnIndexOrThrow("name"));
			
			if(fieldName.equals(name)) {
				close(c);
				return true;
			}
		}
		
		close(c);
		return false;
	}
	
}
