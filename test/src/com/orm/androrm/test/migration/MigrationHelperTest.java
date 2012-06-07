package com.orm.androrm.test.migration;

import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.Model;
import com.orm.androrm.impl.migration.EmptyModel;
import com.orm.androrm.impl.migration.OneFieldModel;
import com.orm.androrm.migration.MigrationHelper;

public class MigrationHelperTest extends AndroidTestCase {

	@Override
	public void setUp() {
		List<Class<? extends Model>> models = new ArrayList<Class<? extends Model>>();
		models.add(EmptyModel.class);
		models.add(OneFieldModel.class);
		
		DatabaseAdapter adapter = new DatabaseAdapter(getContext());
		adapter.setModels(models);
	}
	
	public void testHasField() {
		MigrationHelper helper = new MigrationHelper(getContext());
		
		assertFalse(helper.hasField(EmptyModel.class, "mName"));
		assertTrue(helper.hasField(OneFieldModel.class, "mName"));
	}
	
	public void testTableExists() {
		MigrationHelper helper = new MigrationHelper(getContext());
		
		assertTrue(helper.tableExists("EmptyModel"));
		assertFalse(helper.tableExists("WrongTable"));
	}
	
	@Override
	public void tearDown() {
		DatabaseAdapter adapter = new DatabaseAdapter(getContext());
		adapter.drop();
	}
	
}
