package com.orm.andorm.test.implementation;

import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.FilterSet;
import com.orm.androrm.Model;
import com.orm.androrm.impl.Branch;
import com.orm.androrm.impl.Product;
import com.orm.androrm.impl.Supplier;

public class FieldResulutionTest extends AndroidTestCase {

	private Branch mB1, mB2, mB3;
	private Product mP1;
	private Supplier mS1;
	
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
		mB1 = b1;
		
		// ID 2
		Branch b2 = new Branch();
		b2.setName("Plumblink Pretoria");
		b2.save(getContext());
		mB2 = b2;
		
		// ID 3
		Branch b3 = new Branch();
		b3.setName("The third Branch");
		b3.save(getContext());
		mB3 = b3;
		
		// ID 1
		Product p1 = new Product();
		p1.setName("ofen");
		p1.addBranch(b1);
		p1.addBranch(b3);
		p1.save(getContext());
		mP1 = p1;
		
		Supplier s1 = new Supplier();
		s1.setName("ACME");
		s1.addProduct(p1);
		s1.addBranch(b1);
		s1.save(getContext());
		mS1 = s1;
	}
	
	public void testOneToManyResolutionOnlyField() {
		List<Branch> branches = new ArrayList<Branch>();
		branches.add(mB1);
		branches.add(mB2);
		
		FilterSet filter = new FilterSet();
		filter.in("mBranches", branches);
		
		List<Product> products = Product.filter(getContext(), filter);
		
		assertEquals(1, products.size());
		assertTrue(products.contains(mP1));
		
		branches.remove(0);
		
		filter = new FilterSet();
		filter.in("mBranches", branches);
		
		products = Product.filter(getContext(), filter);
		
		assertEquals(0, products.size());
	}
	
	public void testOneToManyResolutionLastField() {
		List<Branch> branches = new ArrayList<Branch>();
		branches.add(mB1);
		branches.add(mB2);
		
		FilterSet filter = new FilterSet();
		filter.in("mProducts__mBranches", branches);
		
		List<Supplier> suppliers = Supplier.filter(getContext(), filter);
		
		assertEquals(1, suppliers.size());
		assertTrue(suppliers.contains(mS1));
		
		branches.remove(0);
		
		filter = new FilterSet();
		filter.in("mProducts__mBranches", branches);
		
		suppliers = Supplier.filter(getContext(), filter);
		
		assertEquals(0, suppliers.size());
	}
	
	public void testOneToManyResolutionInBetween() {
		FilterSet filter = new FilterSet();
		filter.contains("mProducts__mBranches__mName", "cash");
		
		List<Supplier> suppliers = Supplier.filter(getContext(), filter);
		
		assertEquals(1, suppliers.size());
		assertTrue(suppliers.contains(mS1));
		
		filter = new FilterSet();
		filter.contains("mProducts__mBranches__mName", "plumb");
		
		suppliers = Supplier.filter(getContext(), filter);
		
		assertEquals(0, suppliers.size());
	}
	
	public void testForeignKeyResolutionOnlyField() {
		FilterSet filter = new FilterSet();
		filter.is("mProduct", mP1);
		
		List<Branch> branches = Branch.filter(getContext(), filter);
		
		assertEquals(2, branches.size());
		assertTrue(branches.contains(mB1));
		assertTrue(branches.contains(mB3));
	}
	
	public void testForeignKeyResolutionLastField() {
		FilterSet filter = new FilterSet();
		filter.is("mBranches__mProduct", mP1);
		
		List<Supplier> suppliers = Supplier.filter(getContext(), filter);
		
		assertEquals(1, suppliers.size());
		assertTrue(suppliers.contains(mS1));
	}
	
	public void testForeignKeyResolutionInBetween() {
		FilterSet filter = new FilterSet();
		filter.contains("mBranches__mProduct__mName", "fen");
		
		List<Supplier> suppliers = Supplier.filter(getContext(), filter);
		
		assertEquals(1, suppliers.size());
		assertTrue(suppliers.contains(mS1));
		
		filter = new FilterSet();
		filter.is("mBranches__mProduct__mName", "false");
		
		suppliers = Supplier.filter(getContext(), filter);
		
		assertEquals(0, suppliers.size());
	}
	
	public void testManyToManyFieldResolutionOnlyField() {
		List<Branch> branches = new ArrayList<Branch>();
		branches.add(mB1);
		branches.add(mB2);
		
		FilterSet filter = new FilterSet();
		filter.in("mBranches", branches);
		
		List<Supplier> suppliers = Supplier.filter(getContext(), filter);
		
		assertEquals(1, suppliers.size());
		assertTrue(suppliers.contains(mS1));
		
		branches.remove(0);
		
		filter = new FilterSet();
		filter.in("mBranches", branches);
		
		suppliers = Supplier.filter(getContext(), filter);
		
		assertEquals(0, suppliers.size());
	}
	
	public void testManyToManyFieldResolutionLastField() {
		List<Supplier> suppliers = new ArrayList<Supplier>();
		suppliers.add(mS1);
		
		FilterSet filter = new FilterSet();
		filter.in("mBranches__mSuppliers", suppliers);
		
		List<Product> products = Product.filter(getContext(), filter);
		
		assertEquals(1, products.size());
		assertTrue(products.contains(mP1));
		
		suppliers.clear();
		
		filter = new FilterSet();
		filter.in("mBranches__mSuppliers", suppliers);
		
		products = Product.filter(getContext(), filter);
		
		assertEquals(0, products.size());
	}
	
	public void testManyToManyFieldResolutionInBetween() {
		FilterSet filter = new FilterSet();
		filter.is("mBranches__mSuppliers__mName", "ACME");
		
		List<Product> products = Product.filter(getContext(), filter);
		
		assertEquals(1, products.size());
		assertTrue(products.contains(mP1));
		
		filter = new FilterSet();
		filter.is("mBranches__mSuppliers__mName", "fail");
		
		products = Product.filter(getContext(), filter);
		
		assertEquals(0, products.size());
	}
	
	@Override
	public void tearDown() {
		DatabaseAdapter adapter = new DatabaseAdapter(getContext());
		adapter.drop();
	}
}
