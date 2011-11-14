package com.orm.androrm.test.field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.test.AndroidTestCase;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.Model;
import com.orm.androrm.OneToManyField;
import com.orm.androrm.QuerySet;
import com.orm.androrm.impl.Branch;
import com.orm.androrm.impl.Brand;
import com.orm.androrm.impl.Product;

public class OneToManyFieldTest extends AndroidTestCase {

	@Override
	public void setUp() {
		List<Class<? extends Model>> models = new ArrayList<Class<? extends Model>>();
		models.add(Brand.class);
		models.add(Branch.class);
		models.add(Product.class);
		
		DatabaseAdapter.setDatabaseName("test_db");
		
		DatabaseAdapter adapter = new DatabaseAdapter(getContext());
		adapter.setModels(models);
	}
	
	public void testGetTarget() {
		OneToManyField<Brand, Branch> o = new OneToManyField<Brand, Branch>(Brand.class, Branch.class);
		
		assertEquals(Branch.class, o.getTarget());
	}
	
	public void testAddAndGet() {
		Brand b = new Brand();
		b.setName("Copcal");
		b.save(getContext());
		
		Branch b1 = new Branch();
		b1.setName("test1");
		b1.setBrand(b);
		b1.save(getContext());
		
		Branch b2 = new Branch();
		b2.setName("test2");
		b2.setBrand(b);
		b2.save(getContext());
		
		QuerySet<Branch> branches = b.getBranches(getContext());
		
		b = Brand.objects(getContext()).get(b.getId());
		
		branches = b.getBranches(getContext());
		
		assertEquals(2, branches.count());
		assertTrue(branches.contains(b1));
		assertTrue(branches.contains(b2));
		
		assertEquals(b, b1.getBrand(getContext()));
		assertEquals(b, b2.getBrand(getContext()));
	}
	
	public void testAddAllAndGet() {
		Brand b = new Brand();
		b.setName("Copcal");
		b.save(getContext());
		
		Branch b1 = new Branch();
		b1.setName("test1");
		b1.setBrand(b);
		b1.save(getContext());
		
		Branch b2 = new Branch();
		b2.setName("test2");
		b2.setBrand(b);
		b2.save(getContext());
		
		b = Brand.objects(getContext()).get(b.getId());
		
		QuerySet<Branch> branches = b.getBranches(getContext());
		
		assertEquals(2, branches.count());
		assertTrue(branches.contains(b1));
		assertTrue(branches.contains(b2));
		
		assertEquals(b, b1.getBrand(getContext()));
		assertEquals(b, b2.getBrand(getContext()));
	}
	
	public void testCount() {
		Brand b = new Brand();
		b.setName("Copcal");
		b.save(getContext());
		
		Branch b1 = new Branch();
		b1.setName("test1");
		b1.setBrand(b);
		b1.save(getContext());
		
		Branch b2 = new Branch();
		b2.setName("test2");
		b2.setBrand(b);
		b2.save(getContext());
		
		b = Brand.objects(getContext()).get(b.getId());
		
		assertEquals(2, b.branchCount(getContext()));
	}
	
	public void testReset() {
		Brand b = new Brand();
		b.setName("Copcal");
		b.save(getContext());
		
		Branch b1 = new Branch();
		b1.setName("test1");
		b1.setBrand(b);
		b1.save(getContext());
		
		Branch b2 = new Branch();
		b2.setName("test2");
		b2.setBrand(b);
		b2.save(getContext());
		
		Product p = new Product();
		p.addBranches(Arrays.asList(new Branch[] { b1, b2 }));
		p.save(getContext());
		
		assertEquals(2, p.getBranches(getContext()).count());
		
		p.delete(getContext());
		
		assertEquals(0, p.getBranches(getContext()).count());
	}
	
	@Override
	public void tearDown() {
		DatabaseAdapter adapter = new DatabaseAdapter(getContext());
		adapter.drop();
	}
}
