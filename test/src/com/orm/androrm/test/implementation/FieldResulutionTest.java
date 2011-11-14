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

public class FieldResulutionTest extends AndroidTestCase {

	private Brand mB;
	private Branch mB1, mB2, mB3;
	private Product mP1;
	private Supplier mS1;
	
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
		mB = b;
		
		// ID 1
		Branch b1 = new Branch();
		b1.setName("Cashbuild Pretoria");
		b1.setBrand(b);
		b1.save(getContext());
		mB1 = b1;
		
		// ID 2
		Branch b2 = new Branch();
		b2.setName("Plumblink Pretoria");
		b2.setBrand(b);
		b2.save(getContext());
		mB2 = b2;
		
		// ID 3
		Branch b3 = new Branch();
		b3.setName("The third Branch");
		b3.setBrand(b);
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
		s1.setBrand(b);
		s1.addProduct(p1);
		s1.addBranch(b1);
		s1.save(getContext());
		mS1 = s1;
	}
	
	public void testOneToManyResolutionOnlyField() {
		List<Branch> branches = new ArrayList<Branch>();
		branches.add(mB1);
		branches.add(mB2);
		
		Filter filter = new Filter();
		filter.in("mBranches", branches);
		
		QuerySet<Product> products = Product.objects(getContext()).filter(filter);
		
		assertEquals(1, products.count());
		assertTrue(products.contains(mP1));
		
		branches.remove(0);
		
		filter = new Filter();
		filter.in("mBranches", branches);
		
		products = Product.objects(getContext()).filter(filter);
		
		assertEquals(0, products.count());
	}
	
	public void testOneToManyResolutionLastField() {
		List<Branch> branches = new ArrayList<Branch>();
		branches.add(mB1);
		branches.add(mB2);
		
		Filter filter = new Filter();
		filter.in("mProducts__mBranches", branches);
		
		QuerySet<Supplier> suppliers = Supplier.objects(getContext()).filter(filter);
		
		assertEquals(1, suppliers.count());
		assertTrue(suppliers.contains(mS1));
		
		branches.remove(0);
		
		filter = new Filter();
		filter.in("mProducts__mBranches", branches);
		
		suppliers = Supplier.objects(getContext()).filter(filter);
		
		assertEquals(0, suppliers.count());
	}
	
	public void testOneToManyResolutionInBetween() {
		Filter filter = new Filter();
		filter.contains("mProducts__mBranches__mName", "cash");
		
		QuerySet<Supplier> suppliers = Supplier.objects(getContext()).filter(filter);
		
		assertEquals(1, suppliers.count());
		assertTrue(suppliers.contains(mS1));
		
		filter = new Filter();
		filter.contains("mProducts__mBranches__mName", "plumb");
		
		suppliers = Supplier.objects(getContext()).filter(filter);
		
		assertEquals(0, suppliers.count());
	}
	
	public void testForeignKeyResolutionOnlyField() {
		Filter filter = new Filter();
		filter.is("mBrand", mB);
		
		QuerySet<Branch> branches = Branch.objects(getContext()).filter(filter);
		
		assertEquals(3, branches.count());
		assertTrue(branches.contains(mB1));
		assertTrue(branches.contains(mB3));
	}
	
	public void testForeignKeyResolutionLastField() {
		Filter filter = new Filter();
		filter.is("mBranches__mBrand", mB);
		
		QuerySet<Supplier> suppliers = Supplier.objects(getContext()).filter(filter);
		
		assertEquals(1, suppliers.count());
		assertTrue(suppliers.contains(mS1));
	}
	
	public void testForeignKeyResolutionInBetween() {
		Filter filter = new Filter();
		filter.contains("mBranches__mBrand__mName", "cal");
		
		QuerySet<Supplier> suppliers = Supplier.objects(getContext()).filter(filter);
		
		assertEquals(1, suppliers.count());
		assertTrue(suppliers.contains(mS1));
		
		filter = new Filter();
		filter.is("mBranches__mBrand__mName", "false");
		
		suppliers = Supplier.objects(getContext()).filter(filter);
		
		assertEquals(0, suppliers.count());
	}
	
	public void testManyToManyFieldResolutionOnlyField() {
		List<Branch> branches = new ArrayList<Branch>();
		branches.add(mB1);
		branches.add(mB2);
		
		Filter filter = new Filter();
		filter.in("mBranches", branches);
		
		QuerySet<Supplier> suppliers = Supplier.objects(getContext()).filter(filter);
		
		assertEquals(1, suppliers.count());
		assertTrue(suppliers.contains(mS1));
		
		branches.remove(0);
		
		filter = new Filter();
		filter.in("mBranches", branches);
		
		suppliers = Supplier.objects(getContext()).filter(filter);
		
		assertEquals(0, suppliers.count());
	}
	
	public void testManyToManyFieldResolutionLastField() {
		List<Supplier> suppliers = new ArrayList<Supplier>();
		suppliers.add(mS1);
		
		Filter filter = new Filter();
		filter.in("mBranches__mSuppliers", suppliers);
		
		QuerySet<Product> products = Product.objects(getContext()).filter(filter);
		
		assertEquals(1, products.count());
		assertTrue(products.contains(mP1));
		
		suppliers.clear();
		
		filter = new Filter();
		filter.in("mBranches__mSuppliers", suppliers);
		
		products = Product.objects(getContext()).filter(filter);
		
		assertEquals(0, products.count());
	}
	
	public void testManyToManyFieldResolutionInBetween() {
		Filter filter = new Filter();
		filter.is("mBranches__mSuppliers__mName", "ACME");
		
		QuerySet<Product> products = Product.objects(getContext()).filter(filter);
		
		assertEquals(1, products.count());
		assertTrue(products.contains(mP1));
		
		filter = new Filter();
		filter.is("mBranches__mSuppliers__mName", "fail");
		
		products = Product.objects(getContext()).filter(filter);
		
		assertEquals(0, products.count());
	}
	
	@Override
	public void tearDown() {
		DatabaseAdapter adapter = new DatabaseAdapter(getContext());
		adapter.drop();
	}
}
