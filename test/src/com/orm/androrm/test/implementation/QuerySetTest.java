package com.orm.androrm.test.implementation;

import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.Filter;
import com.orm.androrm.Model;
import com.orm.androrm.QuerySet;
import com.orm.androrm.impl.Branch;
import com.orm.androrm.impl.Brand;
import com.orm.androrm.impl.Product;
import com.orm.androrm.impl.Supplier;

public class QuerySetTest extends AndroidTestCase {

	@Override
	public void setUp() {
		List<Class<? extends Model>> models = new ArrayList<Class<? extends Model>>();
		models.add(Product.class);
		models.add(Branch.class);
		models.add(Supplier.class);
		models.add(Brand.class);
		
		DatabaseAdapter.setDatabaseName("test_db");
		
		DatabaseAdapter adapter = new DatabaseAdapter(getContext());
		adapter.setModels(models);
		
		Brand b = new Brand();
		b.setName("Copcal");
		b.save(getContext());
		
		// ID 1
		Branch b1 = new Branch();
		b1.setName("Cashbuild Pretoria");
		b1.setBrand(b);
		b1.save(getContext());
		
		// ID 2
		Branch b2 = new Branch();
		b2.setName("Plumblink Pretoria");
		b2.setBrand(b);
		b2.save(getContext());
		
		// ID 3
		Branch b3 = new Branch();
		b3.setName("The third Branch");
		b3.setBrand(b);
		b3.save(getContext());
	}
	
	public void testAll() {
		QuerySet<Branch> branches = Branch.objects(getContext()).all();
		
		assertEquals(3, branches.count());
	}
	
	public void testGet() {
		assertEquals("Cashbuild Pretoria", Branch.objects(getContext()).get(1).getName());
		// Triangulation
		assertEquals("Plumblink Pretoria", Branch.objects(getContext()).get(2).getName());
	}
	
	public void testFilter() {
		Filter filter = new Filter();
		filter.contains("mName", "Pretoria");
		
		assertEquals(2, Branch.objects(getContext()).filter(filter).count());
	}
	
	public void testLimit() {
		assertEquals(1, Branch.objects(getContext()).all().limit(1).count());
		assertEquals(2, Branch.objects(getContext()).all().limit(1, 2).count());
	}
	
	public void testContains() {
		Filter filter = new Filter();
		filter.contains("mName", "Pretoria");
		
		Branch contained = Branch.objects(getContext()).get(1);
		Branch notContained = Branch.objects(getContext()).get(3);
		
		QuerySet<Branch> result = Branch.objects(getContext()).filter(filter);
		
		assertTrue(result.contains(contained));
		assertFalse(result.contains(notContained));
	}
	
	public void tearDown() {
		DatabaseAdapter adapter = new DatabaseAdapter(getContext());
		adapter.drop();
	}
}
