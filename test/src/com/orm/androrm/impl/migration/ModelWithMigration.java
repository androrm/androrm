package com.orm.androrm.impl.migration;

import android.content.Context;

import com.orm.androrm.Model;
import com.orm.androrm.field.CharField;
import com.orm.androrm.migration.Migrator;

public class ModelWithMigration extends Model {

	public ModelWithMigration() {
		super();
	}
	
	@Override
	protected void migrate(Context context) {
		Migrator<ModelWithMigration> migrator = new Migrator<ModelWithMigration>(ModelWithMigration.class);
		migrator.addField("mTestField", new CharField());
		migrator.migrate(context);
	}
	
}
