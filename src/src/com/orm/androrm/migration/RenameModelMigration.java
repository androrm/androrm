package com.orm.androrm.migration;

import android.content.Context;
import android.database.SQLException;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.DatabaseBuilder;
import com.orm.androrm.Model;

public class RenameModelMigration<T extends Model> extends AndrormMigration<T> {

	protected String mOldName;
	
	public RenameModelMigration(String old) {
		super(null, "rename_table");
		
		mOldName = old.toLowerCase();
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
		
		DatabaseAdapter adapter = new DatabaseAdapter(context);
		
		try {
			adapter.renameTable(mOldName, getValue(model));
		} catch (SQLException e) {
			return false;
		}
		
		renameRelationTables(context, model);
		
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
