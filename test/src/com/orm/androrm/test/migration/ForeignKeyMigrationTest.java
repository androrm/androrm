package com.orm.androrm.test.migration;

import java.util.ArrayList;
import java.util.List;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.Model;
import com.orm.androrm.field.ForeignKeyField;
import com.orm.androrm.impl.Product;
import com.orm.androrm.impl.migration.EmptyModel;
import com.orm.androrm.migration.Migrator;

public class ForeignKeyMigrationTest extends AbstractMigrationTest {

	@Override
	public void setUp() {
		List<Class<? extends Model>> models = new ArrayList<Class<? extends Model>>();
		models.add(EmptyModel.class);
		
		DatabaseAdapter adapter = DatabaseAdapter.getInstance(getContext());
		adapter.setModels(models);
		
		super.setUp();
	}
	
	public void testForeignKeyAdd() {
		assertFalse(mHelper.hasField(EmptyModel.class, "mProduct"));
		
		Migrator<EmptyModel> migrator = new Migrator<EmptyModel>(EmptyModel.class);
		migrator.addField("mProduct", new ForeignKeyField<Product>(Product.class));
		migrator.migrate(getContext());
		
		assertTrue(mHelper.hasField(EmptyModel.class, "mProduct"));
	}
	
}
