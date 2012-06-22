package com.orm.androrm.test.discovery;

import java.util.List;

import android.test.AndroidTestCase;

import com.orm.androrm.Model;
import com.orm.androrm.discovery.AndrormDiscoveryService;
import com.orm.androrm.impl.migration.EmptyModel;
import com.orm.androrm.impl.migration.ModelWithMigration;
import com.orm.androrm.impl.migration.ModelWithRelation;
import com.orm.androrm.impl.migration.NewEmptyModel;
import com.orm.androrm.impl.migration.OneFieldModel;

public class ModelDiscoveryTest extends AndroidTestCase {

	public void testSimpleDiscovery() {
		AndrormDiscoveryService discoveryService = new AndrormDiscoveryService();
		
		List<Class<? extends Model>> models = discoveryService.discover("com.orm.androrm.impl.migration");
		
		assertTrue(5 == models.size());
		
		assertTrue(models.contains(EmptyModel.class));
		assertTrue(models.contains(ModelWithMigration.class));
		assertTrue(models.contains(ModelWithRelation.class));
		assertTrue(models.contains(NewEmptyModel.class));
		assertTrue(models.contains(OneFieldModel.class));
	}
	
}
