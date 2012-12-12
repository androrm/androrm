package com.orm.androrm.test.database;

import java.util.ArrayList;
import java.util.List;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.Model;
import com.orm.androrm.impl.BlankModel;

import android.test.AndroidTestCase;

public class TransactionTest extends AndroidTestCase {

	@Override
	public void setUp() {
		List<Class<? extends Model>> models = new ArrayList<Class<? extends Model>>();
		models.add(BlankModel.class);

		DatabaseAdapter adapter = DatabaseAdapter.getInstance(getContext());
		adapter.setModels(models);
	}
	
	public void testRollback() {
		assertEquals(0, BlankModel.objects(getContext()).count());
		
		DatabaseAdapter adapter = DatabaseAdapter.getInstance(getContext());
		adapter.beginTransaction();
		
		BlankModel b1 = new BlankModel();
		b1.save(getContext());
		
		BlankModel b2 = new BlankModel();
		b2.save(getContext());
		
		adapter.rollbackTransaction();
		
		assertEquals(0, BlankModel.objects(getContext()).count());
	}
	
	public void testCommit() {
		assertEquals(0, BlankModel.objects(getContext()).count());
		
		DatabaseAdapter adapter = DatabaseAdapter.getInstance(getContext());
		adapter.beginTransaction();
		
		BlankModel b1 = new BlankModel();
		b1.save(getContext());
		
		BlankModel b2 = new BlankModel();
		b2.save(getContext());
		
		adapter.commitTransaction();
		
		assertEquals(2, BlankModel.objects(getContext()).count());
	}
	
	@Override
	public void tearDown() {
		DatabaseAdapter adapter = DatabaseAdapter.getInstance(getContext());
		adapter.drop();
	}
}
