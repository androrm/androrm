package com.orm.androrm.test.cache;

import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;

import com.orm.androrm.CharField;
import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.Model;
import com.orm.androrm.ModelCache;
import com.orm.androrm.impl.BlankModel;

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

		DatabaseAdapter adapter = new DatabaseAdapter(getContext());
		adapter.setModels(models);
		
		assertTrue(ModelCache.modelHasField(BlankModel.class, "mName"));
		
		adapter.drop();
	}
	
	public void tearDown() {
		ModelCache.reset();
	}
}
