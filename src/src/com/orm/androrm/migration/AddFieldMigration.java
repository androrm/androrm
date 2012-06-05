package com.orm.androrm.migration;

import android.content.Context;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.DatabaseBuilder;
import com.orm.androrm.DatabaseField;
import com.orm.androrm.Filter;
import com.orm.androrm.Model;

public class AddFieldMigration extends AndrormMigration<DatabaseField<?>> {

	public AddFieldMigration(String name, DatabaseField<?> field) {
		super(name, field);

		ACTION = "add";
	}

	private boolean isApplied(Class<? extends Model> model, Context context) {
		Filter filter = getFilter(model);
		
		return !Migration.objects(context).filter(filter).isEmpty();
	}

	@Override
	public boolean execute(Class<? extends Model> model, Context context) {
		if(isApplied(model, context)) {
			return true;
		}
		
		String sql = "ALTER TABLE `" + DatabaseBuilder.getTableName(model) + "` " +
				"ADD COLUMN " + mFieldInstance.getDefinition(mName);
		
		DatabaseAdapter adapter = new DatabaseAdapter(context);
		
		adapter.open();
		adapter.query(sql);
		adapter.close();
		
		// TODO: check query result (exception!?)
		return true;
	}

}
