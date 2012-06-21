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
