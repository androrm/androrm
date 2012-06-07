package com.orm.androrm.test.migration;

import java.util.ArrayList;
import java.util.List;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.Model;
import com.orm.androrm.impl.migration.EmptyModel;
import com.orm.androrm.impl.migration.NewEmptyModel;
import com.orm.androrm.migration.Migrator;


public class RenameModelMigrationTest extends AbstractMigrationTest {

	@Override
	public void setUp() {
		List<Class<? extends Model>> models = new ArrayList<Class<? extends Model>>();
		models.add(EmptyModel.class);
		models.add(NewEmptyModel.class);
		
		DatabaseAdapter adapter = new DatabaseAdapter(getContext());
		adapter.setModels(models);
		
		super.setUp();
	}
	
	public void testRenameTable() {
		Migrator<NewEmptyModel> migrator = new Migrator<NewEmptyModel>(NewEmptyModel.class);
		
		assertTrue(mHelper.tableExists(EmptyModel.class));
		
		migrator.renameTable("EmptyModel", NewEmptyModel.class);
		migrator.migrate(getContext());
		
		assertFalse(mHelper.tableExists(EmptyModel.class));
		assertTrue(mHelper.tableExists(NewEmptyModel.class));
		
	}
	
}
