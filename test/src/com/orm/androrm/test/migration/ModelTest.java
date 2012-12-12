package com.orm.androrm.test.migration;

import java.util.ArrayList;
import java.util.List;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.Model;
import com.orm.androrm.impl.migration.ModelWithMigration;


public class ModelTest extends AbstractMigrationTest {

	public void testMigrationsRun() {
		List<Class<? extends Model>> models = new ArrayList<Class<? extends Model>>();
		models.add(ModelWithMigration.class);
		
		DatabaseAdapter adapter = DatabaseAdapter.getInstance(getContext());
		adapter.setModels(models);
		
		assertTrue(mHelper.hasField(ModelWithMigration.class, "mTestField"));
	}
	
}
