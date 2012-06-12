package com.orm.androrm.test.migration;

import com.orm.androrm.field.ManyToManyField;
import com.orm.androrm.impl.migration.EmptyModel;
import com.orm.androrm.impl.migration.OneFieldModel;
import com.orm.androrm.migration.Migrator;

public class ManyToManyFieldMigrationTest extends AbstractMigrationTest {

	public void testTableCreate() {
		ManyToManyField<EmptyModel, OneFieldModel> field = new ManyToManyField<EmptyModel, OneFieldModel>(EmptyModel.class, OneFieldModel.class); 
		
		assertFalse(mHelper.tableExists(field.getRelationTableName()));
		
		Migrator<EmptyModel> migrator = new Migrator<EmptyModel>(EmptyModel.class);
		migrator.addField("mModels", field);
		migrator.migrate(getContext());
		
		assertTrue(mHelper.tableExists(field.getRelationTableName()));
	}
	
}
