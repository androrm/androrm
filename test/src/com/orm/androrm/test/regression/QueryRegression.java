package com.orm.androrm.test.regression;

import java.util.ArrayList;
import java.util.List;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.Model;
import com.orm.androrm.impl.Transaction;

import android.test.AndroidTestCase;

public class QueryRegression extends AndroidTestCase {

	public void testDumbClassName() {
		List<Class<? extends Model>> models = new ArrayList<Class<? extends Model>>();
		models.add(Transaction.class);
		
		DatabaseAdapter adapter = DatabaseAdapter.getInstance(getContext());
		
		try {
			adapter.setModels(models);
		} catch (Exception e) {
			fail();
		}
	}
	
}
