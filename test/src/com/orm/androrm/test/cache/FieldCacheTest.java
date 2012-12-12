package com.orm.androrm.test.cache;

import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.Model;
import com.orm.androrm.ModelCache;
import com.orm.androrm.field.CharField;
import com.orm.androrm.impl.BlankModel;
import com.orm.androrm.impl.BlankModelNoAutoincrement;

public class FieldCacheTest extends AndroidTestCase {

	public class TestModel extends Model {
		
		protected CharField mName;
		
		public TestModel() {
			super();
			
			mName = new CharField();
		}
		
	}
	
	public void testAddModel() {
		assertFalse(ModelCache.knowsModel(TestModel.class));
		
		ModelCache.addModel(TestModel.class);
		
		assertTrue(ModelCache.knowsModel(TestModel.class));
	}
	
	public void testHasField() {
		assertFalse(ModelCache.modelHasField(BlankModel.class, "mName"));
		
		List<Class<? extends Model>> models = new ArrayList<Class<? extends Model>>();
		models.add(BlankModel.class);

		DatabaseAdapter adapter = DatabaseAdapter.getInstance(getContext());
		adapter.setModels(models);
		
		assertTrue(ModelCache.modelHasField(BlankModel.class, "mName"));
	}
	
	public void testFieldShortcur() {
		assertNull(ModelCache.getField(BlankModelNoAutoincrement.class, "mName"));
		
		List<Class<? extends Model>> models = new ArrayList<Class<? extends Model>>();
		models.add(BlankModelNoAutoincrement.class);
		
		DatabaseAdapter adapter = DatabaseAdapter.getInstance(getContext());
		adapter.setModels(models);
		
		assertNotNull(ModelCache.getField(BlankModelNoAutoincrement.class, "mName"));
	}
	
	public void tearDown() {
		DatabaseAdapter adapter = DatabaseAdapter.getInstance(getContext());
		adapter.drop();
		
		ModelCache.reset();
	}
}
