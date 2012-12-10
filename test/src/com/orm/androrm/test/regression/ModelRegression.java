package com.orm.androrm.test.regression;

import java.util.ArrayList;
import java.util.List;

import android.database.SQLException;
import android.test.AndroidTestCase;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.Model;
import com.orm.androrm.impl.migration.EmptyModel;

public class ModelRegression extends AndroidTestCase {

	@Override
	public void setUp() {
		List<Class<? extends Model>> models = new ArrayList<Class<? extends Model>>();
		models.add(EmptyModel.class);
		
		DatabaseAdapter adapter = DatabaseAdapter.getInstance(getContext());
		adapter.setModels(models);
	}
	
	public void testEmptyModel() {
		EmptyModel model = new EmptyModel();
		
		try {
			model.save(getContext());
		} catch (SQLException e) {
			fail();
		}
	}
	
	@Override
	public void tearDown() {
		DatabaseAdapter adapter = DatabaseAdapter.getInstance(getContext());
		adapter.drop();
	}
	
}
