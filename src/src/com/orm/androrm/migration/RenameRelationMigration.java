package com.orm.androrm.migration;

import java.util.List;

import android.content.Context;
import android.database.SQLException;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.Model;

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
		adapter.open();
		
		for(String table : tables) {
			String sql = "ALTER TABLE `" + table + "` RENAME TO `" + table.replace(mOldName, newName) + "`";
			
			try {
				adapter.exec(sql);
			} catch(SQLException e) {
				adapter.close();
				
				return false;
			}
		}
		
		adapter.close();
		
		return true;
	}

}
