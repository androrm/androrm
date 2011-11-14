package com.orm.androrm.test.field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.test.AndroidTestCase;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.ForeignKeyField;
import com.orm.androrm.ManyToManyField;
import com.orm.androrm.Model;
import com.orm.androrm.QuerySet;
import com.orm.androrm.impl.Branch;
import com.orm.androrm.impl.Brand;
import com.orm.androrm.impl.Product;
import com.orm.androrm.impl.Supplier;

public class ManyToManyFieldTest extends AndroidTestCase {

	@Override
	public void setUp() {
		DatabaseAdapter.setDatabaseName("test_db");

		List<Class<? extends Model>> models = new ArrayList<Class<? extends Model>>();
		models.add(Product.class);
		models.add(Supplier.class);
		models.add(Branch.class);
		models.add(Brand.class);
		
		DatabaseAdapter adapter = new DatabaseAdapter(getContext());
		adapter.setModels(models);
	}

	public void testRelationTableName() {
		ManyToManyField<Supplier, Product> m = new ManyToManyField<Supplier, Product>(Supplier.class, Product.class);

		// table name should be lexical ordering of involved model tables
		String tableName = "product_supplier";
		assertEquals(tableName, m.getRelationTableName());
	}

	public void testGetTarget() {
		ManyToManyField<Supplier, Product> m = new ManyToManyField<Supplier, Product>(Supplier.class, Product.class);

		assertEquals(Product.class, m.getTarget());
	}

	public void testGetFieldDescriptors() {
		ForeignKeyField<Supplier> left = new ForeignKeyField<Supplier>(Supplier.class);
		ForeignKeyField<Product> right = new ForeignKeyField<Product>(Product.class);

		ManyToManyField<Supplier, Product> m = new ManyToManyField<Supplier, Product>(Supplier.class, Product.class);

		assertEquals(left.getDefinition("foo"), m.getLeftLinkDescriptor().getDefinition("foo"));
		assertEquals(left.getConstraint("foo"), m.getLeftLinkDescriptor().getConstraint("foo"));

		assertEquals(right.getDefinition("foo"), m.getRightHandDescriptor().getDefinition("foo"));
		assertEquals(right.getConstraint("foo"), m.getRightHandDescriptor().getConstraint("foo"));
	}

	public void testAddAndGet() {
		Product p1 = new Product();
		p1.setName("test1");
		p1.save(getContext());

		Product p2 = new Product();
		p2.setName("test2");
		p2.save(getContext());
		
		Brand b = new Brand();
		b.setName("Copcal");
		b.save(getContext());

		Supplier s = new Supplier();
		s.setName("ACME");
		s.setBrand(b);
		s.addProduct(p1);
		s.addProduct(p2);
		s.save(getContext());

		s = Supplier.objects(getContext()).get(s.getId());

		QuerySet<Product> products = s.getProducts(getContext());

		assertEquals(2, products.count());
		assertTrue(products.contains(p1));
		assertTrue(products.contains(p2));
	}

	public void testAddAllAndGet() {
		Product p1 = new Product();
		p1.setName("test1");
		p1.save(getContext());

		Product p2 = new Product();
		p2.setName("test2");
		p2.save(getContext());
		
		Brand b = new Brand();
		b.setName("Copcal");
		b.save(getContext());

		Supplier s = new Supplier();
		s.setName("ACME");
		s.setBrand(b);
		s.addProducts(Arrays.asList(new Product[] { p1, p2 }));
		s.save(getContext());

		s = Supplier.objects(getContext()).get(s.getId());

		QuerySet<Product> products = s.getProducts(getContext());

		assertEquals(2, products.count());
		assertTrue(products.contains(p1));
		assertTrue(products.contains(p2));
	}

	public void testCount() {
		Product p1 = new Product();
		p1.setName("test1");
		p1.save(getContext());

		Product p2 = new Product();
		p2.setName("test2");
		p2.save(getContext());
		
		Brand b = new Brand();
		b.setName("Copcal");
		b.save(getContext());

		Supplier s = new Supplier();
		s.setName("ACME");
		s.setBrand(b);
		s.addProducts(Arrays.asList(new Product[] { p1, p2 }));

		assertEquals(0, s.productCount(getContext()));

		s.save(getContext());
		s = Supplier.objects(getContext()).get(s.getId());

		assertEquals(2, s.productCount(getContext()));
	}

	public void testSet() {
		Product p = new Product();
		p.setName("test product");
		p.save(getContext());
		
		Brand b = new Brand();
		b.setName("Copcal");
		b.save(getContext());
		
		Branch b1 = new Branch();
		b1.setName("test branch");
		b1.setBrand(b);
		b1.addProduct(p);
		b1.addProduct(p);

		b1.save(getContext());

		Branch b2 = Model.objects(getContext(), Branch.class).get(b1.getId());

		assertEquals(1, b2.getProducts(getContext()).count());
		assertTrue(b2.getProducts(getContext()).contains(p));
	}

	public void testReset() {
		Product p1 = new Product();
		p1.setName("test1");
		p1.save(getContext());

		Product p2 = new Product();
		p2.setName("test2");
		p2.save(getContext());
		
		Brand b = new Brand();
		b.setName("Copcal");
		b.save(getContext());

		Supplier s = new Supplier();
		s.setName("ACME");
		s.setBrand(b);
		s.addProducts(Arrays.asList(new Product[] { p1, p2 }));
		s.save(getContext());

		assertEquals(2, s.getProducts(getContext()).count());

		s.delete(getContext());

		assertEquals(0, s.getProducts(getContext()).count());
	}

	@Override
	public void tearDown() {
		DatabaseAdapter adapter = new DatabaseAdapter(getContext());
		adapter.drop();
	}
}
