package com.orm.andorm.test.implementation;

import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.FilterSet;
import com.orm.androrm.Model;
import com.orm.androrm.QuerySet;
import com.orm.androrm.impl.Branch;
import com.orm.androrm.impl.Product;
import com.orm.androrm.impl.Supplier;

public class QuerySetTest extends AndroidTestCase {

	@Override
	public void setUp() {
		List<Class<? extends Model>> models = new ArrayList<Class<? extends Model>>();
		models.add(Product.class);
		models.add(Branch.class);
		models.add(Supplier.class);
		
		DatabaseAdapter.setDatabaseName("test_db");
		
		DatabaseAdapter adapter = new DatabaseAdapter(getContext());
		adapter.setModels(models);
		
		// ID 1
		Branch b1 = new Branch();
		b1.setName("Cashbuild Pretoria");
		b1.save(getContext());
		
		// ID 2
		Branch b2 = new Branch();
		b2.setName("Plumblink Pretoria");
		b2.save(getContext());
		
		// ID 3
		Branch b3 = new Branch();
		b3.setName("The third Branch");
		b3.save(getContext());
	}
	
	public void testGeneralObject() {
		FilterSet filter = new FilterSet();
		filter.contains("mName", "pretoria");
		
		FilterSet filter2 = new FilterSet();
		filter2.contains("mName", "plumb");
		
		QuerySet<Branch> branches = Branch.objects(getContext()).filter(filter2).filter(filter);
		
		for(Branch b : branches) {
			int id = b.getId();
		}
		
	}
	
	public void tearDown() {
		DatabaseAdapter adapter = new DatabaseAdapter(getContext());
		adapter.drop();
	}
}
