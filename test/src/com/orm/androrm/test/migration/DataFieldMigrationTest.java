package com.orm.androrm.test.migration;

import java.util.ArrayList;
import java.util.List;

import com.orm.androrm.CharField;
import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.Model;
import com.orm.androrm.impl.migration.EmptyModel;
import com.orm.androrm.migration.MigrationHelper;
import com.orm.androrm.migration.Migrator;

import android.test.AndroidTestCase;

public class DataFieldMigrationTest extends AndroidTestCase {

	private MigrationHelper mHelper;
	
	@Override
	public void setUp() {
		List<Class<? extends Model>> models = new ArrayList<Class<? extends Model>>();
		models.add(EmptyModel.class);
		
		DatabaseAdapter adapter = new DatabaseAdapter(getContext());
		adapter.setModels(models);
		
		mHelper = new MigrationHelper(getContext());
	}
	
	public void testFieldAdd() {
		Migrator<EmptyModel> migrator = new Migrator<EmptyModel>(EmptyModel.class);
		
		assertFalse(mHelper.hasField(EmptyModel.class, "mName"));
		
		migrator.addField("mName", new CharField());
		migrator.migrate(getContext());
		
		assertTrue(mHelper.hasField(EmptyModel.class, "mName"));
	}
	
	@Override
	public void tearDown() {
		DatabaseAdapter adapter = new DatabaseAdapter(getContext());
		adapter.drop();
	}
	
}
