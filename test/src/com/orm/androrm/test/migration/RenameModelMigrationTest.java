package com.orm.androrm.test.migration;

import java.util.ArrayList;
import java.util.List;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.Filter;
import com.orm.androrm.Model;
import com.orm.androrm.impl.migration.EmptyModel;
import com.orm.androrm.impl.migration.ModelWithRelation;
import com.orm.androrm.impl.migration.NewEmptyModel;
import com.orm.androrm.impl.migration.NewModelWithRelation;
import com.orm.androrm.migration.Migration;
import com.orm.androrm.migration.Migrator;


public class RenameModelMigrationTest extends AbstractMigrationTest {

	@Override
	public void setUp() {
		List<Class<? extends Model>> models = new ArrayList<Class<? extends Model>>();
		models.add(EmptyModel.class);
		models.add(ModelWithRelation.class);
		models.add(NewEmptyModel.class);
		models.add(NewModelWithRelation.class);
		
		DatabaseAdapter adapter = DatabaseAdapter.getInstance(getContext());
		adapter.setModels(models);
		
		super.setUp();
	}
	
	public void testRenameTable() {
		Migrator<NewEmptyModel> migrator = new Migrator<NewEmptyModel>(NewEmptyModel.class);
		
		assertTrue(mHelper.tableExists(EmptyModel.class));
		
		EmptyModel model1 = new EmptyModel();
		model1.save(getContext());
		
		assertEquals(1, EmptyModel.objects(getContext()).count());
		assertEquals(0, NewEmptyModel.objects(getContext()).count());
		
		migrator.renameModel("EmptyModel", NewEmptyModel.class);
		migrator.migrate(getContext());
		
		assertFalse(mHelper.tableExists(EmptyModel.class));
		
		assertEquals(1, NewEmptyModel.objects(getContext()).count());
		
		Filter filter = new Filter();
		filter.is("mModel", "newemptymodel")
			  .is("mAction", "rename_table")
			  .is("mValue", "newemptymodel");
		
		assertEquals(1, Migration.objects(getContext()).filter(filter).count());
	}
	
	public void testRenameRelation() {
		Migrator<NewModelWithRelation> migrator = new Migrator<NewModelWithRelation>(NewModelWithRelation.class);
		
		assertTrue(mHelper.hasRelationTable(ModelWithRelation.class));
		assertTrue(mHelper.tableExists("emptymodel_modelwithrelation"));
		
		ModelWithRelation model1 = new ModelWithRelation();
		model1.save(getContext());

		EmptyModel empty = new EmptyModel();
		empty.save(getContext());
		
		model1.addRelation(empty);
		model1.save(getContext());
		
		assertEquals(1, model1.getRelations(getContext()).count());
		assertEquals(0, NewModelWithRelation.objects(getContext()).count());
		
		migrator.renameModel("ModelWithRelation", NewModelWithRelation.class);
		migrator.migrate(getContext());
		
		NewModelWithRelation one = NewModelWithRelation.objects(getContext()).get(1);
		assertEquals(1, one.getRelations(getContext()).count());
		
		assertTrue(mHelper.hasRelationTable(NewModelWithRelation.class));
		assertFalse(mHelper.hasRelationTable(ModelWithRelation.class));
		assertFalse(mHelper.tableExists("emptymodel_modelwithrelation"));
		
		Filter filter = new Filter();
		filter.is("mModel", "newmodelwithrelation")
		      .is("mAction", "rename_relation")
		      .is("mValue", "emptymodel_modelwithrelation");
		
		assertEquals(1, Migration.objects(getContext()).filter(filter).count());
	}

}
