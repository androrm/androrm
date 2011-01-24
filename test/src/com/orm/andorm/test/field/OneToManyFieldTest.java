package com.orm.andorm.test.field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.test.AndroidTestCase;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.Model;
import com.orm.androrm.OneToManyField;
import com.orm.androrm.impl.Branch;
import com.orm.androrm.impl.Product;

public class OneToManyFieldTest extends AndroidTestCase {

	@Override
	public void setUp() {
		List<Class<? extends Model>> models = new ArrayList<Class<? extends Model>>();
		models.add(Product.class);
		models.add(Branch.class);
		
		DatabaseAdapter.setDatabaseName("test_db");
		
		DatabaseAdapter adapter = new DatabaseAdapter(getContext());
		adapter.setModels(models);
	}
	
	public void testGetTarget() {
		OneToManyField<Product, Branch> o = new OneToManyField<Product, Branch>(Product.class, Branch.class);
		
		assertEquals(Branch.class, o.getTarget());
	}
	
	public void testAddAndGet() {
		Branch b1 = new Branch();
		b1.setName("test1");
		b1.save(getContext());
		
		Branch b2 = new Branch();
		b2.setName("test2");
		b2.save(getContext());
		
		Product p = new Product();
		p.addBranch(b1);
		p.addBranch(b2);
		
		List<Branch> branches = p.getBranches(getContext());
		
		p.save(getContext());
		
		p = Product.get(getContext(), p.getId());
		
		branches = p.getBranches(getContext());
		
		assertEquals(2, branches.size());
		assertTrue(branches.contains(b1));
		assertTrue(branches.contains(b2));
		
		assertEquals(p, b1.getProduct(getContext()));
		assertEquals(p, b2.getProduct(getContext()));
	}
	
	public void testAddAllAndGet() {
		Branch b1 = new Branch();
		b1.setName("test1");
		b1.save(getContext());
		
		Branch b2 = new Branch();
		b2.setName("test2");
		b2.save(getContext());
		
		Product p = new Product();
		p.addBranches(Arrays.asList(new Branch[] { b1, b2 }));
		p.save(getContext());
		
		p = Product.get(getContext(), p.getId());
		
		List<Branch> branches = p.getBranches(getContext());
		
		assertEquals(2, branches.size());
		assertTrue(branches.contains(b1));
		assertTrue(branches.contains(b2));
		
		assertEquals(p, b1.getProduct(getContext()));
		assertEquals(p, b2.getProduct(getContext()));
	}
	
	public void testCount() {
		Branch b1 = new Branch();
		b1.setName("test1");
		b1.save(getContext());
		
		Branch b2 = new Branch();
		b2.setName("test2");
		b2.save(getContext());
		
		Product p = new Product();
		p.addBranches(Arrays.asList(new Branch[] { b1, b2 }));
		
		assertEquals(2, p.branchCount(getContext()));
		
		p.save(getContext());
		p = Product.get(getContext(), p.getId());
		
		assertEquals(2, p.branchCount(getContext()));
	}
	
	public void testOrdering() {
		Branch b1 = new Branch();
		b1.setName("zzz");
		b1.save(getContext());
		
		Branch b2 = new Branch();
		b2.setName("aaa");
		b2.save(getContext());
		
		Product p = new Product();
		p.addBranch(b1);
		p.addBranch(b2);
		p.save(getContext());
		
		p = Product.get(getContext(), p.getId());
		
		List<Branch> branches = p.getBranches(getContext());
		
		assertEquals(b2, branches.get(0));
		assertEquals(b1, branches.get(1));
	}
	
	@Override
	public void tearDown() {
		DatabaseAdapter adapter = new DatabaseAdapter(getContext());
		adapter.drop();
	}
}
