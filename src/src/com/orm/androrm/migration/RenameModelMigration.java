package com.orm.androrm.migration;

import android.content.Context;
import android.database.SQLException;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.DatabaseBuilder;
import com.orm.androrm.Model;

public class RenameModelMigration extends AndrormMigration {

	private String mOldName;
	
	public RenameModelMigration(String old) {
		super(null, "rename_table");
		
		mOldName = old.toLowerCase();
	}

	@Override
	public boolean execute(Class<? extends Model> model, Context context) {
		if(isApplied(model, context)) {
			return false;
		}
		
		DatabaseAdapter adapter = new DatabaseAdapter(context);
		adapter.drop(getValue(model));
		
		String sql = "ALTER TABLE `" + mOldName + "` "
					+ "RENAME TO `" + getValue(model) + "`";
		
		adapter.open();
		
		try {
			adapter.exec(sql);
		} catch (SQLException e) {
			adapter.close();
			
			return false;
		}
		
		adapter.close();
		
		return true;
	}

	@Override
	public String getValue(Class<? extends Model> model) {
		return DatabaseBuilder.getTableName(model);
	}

}
