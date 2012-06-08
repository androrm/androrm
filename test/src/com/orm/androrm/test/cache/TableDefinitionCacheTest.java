package com.orm.androrm.test.cache;

import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.Model;
import com.orm.androrm.ModelCache;
import com.orm.androrm.impl.Product;

public class TableDefinitionCacheTest extends AndroidTestCase {

	public void testTableDefinitionCache() {
		assertFalse(ModelCache.knowsModel(Product.class));
		assertNull(ModelCache.getTableDefinitions(Product.class));
		
		List<Class<? extends Model>> models = new ArrayList<Class<? extends Model>>();
		models.add(Product.class);
		
		DatabaseAdapter adapter = new DatabaseAdapter(getContext());
		adapter.setModels(models);
		
		assertTrue(ModelCache.knowsModel(Product.class));
		assertNotNull(ModelCache.getTableDefinitions(Product.class));
	}
	
	public void tearDown() {
		DatabaseAdapter adapter = new DatabaseAdapter(getContext());
		adapter.drop();
		
		ModelCache.reset();
	}
	
}
