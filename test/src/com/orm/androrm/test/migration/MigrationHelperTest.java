package com.orm.androrm.test.migration;

import java.util.ArrayList;
import java.util.List;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.Model;
import com.orm.androrm.impl.migration.EmptyModel;
import com.orm.androrm.impl.migration.ModelWithRelation;
import com.orm.androrm.impl.migration.OneFieldModel;

public class MigrationHelperTest extends AbstractMigrationTest {

	@Override
	public void setUp() {
		List<Class<? extends Model>> models = new ArrayList<Class<? extends Model>>();
		models.add(EmptyModel.class);
		models.add(OneFieldModel.class);
		models.add(ModelWithRelation.class);
		
		DatabaseAdapter adapter = DatabaseAdapter.getInstance(getContext());
		adapter.setModels(models);
		
		super.setUp();
	}
	
	public void testHasField() {
		assertFalse(mHelper.hasField(EmptyModel.class, "mName"));
		assertTrue(mHelper.hasField(OneFieldModel.class, "mName"));
	}
	
	public void testTableExists() {
		assertTrue(mHelper.tableExists(EmptyModel.class));
		assertTrue(mHelper.tableExists("EmptyModel"));
		
		assertFalse(mHelper.tableExists("WrongTable"));
	}
	
	public void testHasRelationTable() {
		assertTrue(mHelper.hasRelationTable(ModelWithRelation.class));
		assertTrue(mHelper.hasRelationTable("ModelWithRelation"));
	}
	
}
