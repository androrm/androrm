package com.orm.androrm.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.Rule;
import com.orm.androrm.Filter;
import com.orm.androrm.InStatement;
import com.orm.androrm.LikeStatement;
import com.orm.androrm.Model;
import com.orm.androrm.Statement;
import com.orm.androrm.impl.Branch;
import com.orm.androrm.impl.Product;
import com.orm.androrm.impl.Supplier;

import android.test.AndroidTestCase;

public class FilterTest extends AndroidTestCase {

	@Override
	public void setUp() {
		List<Class<? extends Model>> models = new ArrayList<Class<? extends Model>>();
		models.add(Product.class);
		models.add(Branch.class);
		models.add(Supplier.class);
		
		DatabaseAdapter.setDatabaseName("test_db");
		
		DatabaseAdapter adapter = new DatabaseAdapter(getContext());
		adapter.setModels(models);
	}
	
	public void testIs() {
		Filter f = new Filter();
		f.is("product__name", "foo");
		
		List<Rule> filters = f.getRules();
		Rule filter = filters.get(0);
		Statement s = filter.getStatement();
		Set<String> keys = s.getKeys();
		
		assertEquals("product__name", filter.getKey());
		assertTrue(keys.contains("name"));
		assertEquals("name = 'foo'", s.toString());
		
		Product p = new Product();
		p.setName("test product");
		p.save(getContext());
		
		f = new Filter();
		f.is("supplier__product", p);
		
		filters = f.getRules();
		filter = filters.get(0);
		s = filter.getStatement();
		keys = s.getKeys();
		
		assertEquals("supplier__product", filter.getKey());
		assertTrue(keys.contains("product"));
		assertEquals("product = '" + p.getId() + "'", s.toString());
	}
	
	public void testIn() {
		Filter set = new Filter();

		List<Integer> values = new ArrayList<Integer>();
		values.add(3);
		values.add(5);
		
		set.in("product__id", values);
		
		List<Rule> filters = set.getRules();
		Rule filter = filters.get(0);
		Statement s = filter.getStatement();
		Set<String> keys = s.getKeys();
		
		assertEquals("product__id", filter.getKey());
		assertTrue(keys.contains("id"));
		assertTrue(s instanceof InStatement);
		assertEquals("id IN (3,5)", s.toString());
		
		List<Product> products = new ArrayList<Product>();
		Product p1 = new Product();
		p1.setName("test 1");
		p1.save(getContext());
		
		Product p2 = new Product();
		p2.setName("test 2");
		p2.save(getContext());
		
		products.add(p1);
		products.add(p2);
		
		set = new Filter();
		set.in("supplier__product", products);
		
		filters = set.getRules();
		filter = filters.get(0);
		s = filter.getStatement();
		keys = s.getKeys();
		
		assertEquals("supplier__product", filter.getKey());
		assertTrue(keys.contains("product"));
		assertTrue(s instanceof InStatement);
		assertEquals("product IN (" + p1.getId() + "," + p2.getId() + ")", s.toString());
	}
	
	public void testContains() {
		Filter set = new Filter();
		set.contains("supplier__name", "foo");
		
		List<Rule> filters = set.getRules();
		Rule filter = filters.get(0);
		Statement s = filter.getStatement();
		Set<String> keys = s.getKeys();
		
		assertEquals("supplier__name", filter.getKey());
		assertTrue(keys.contains("name"));
		assertTrue(s instanceof LikeStatement);
		assertEquals("name LIKE '%foo%'", s.toString());
	}
	
	@Override
	public void tearDown() {
		DatabaseAdapter adapter = new DatabaseAdapter(getContext());
		adapter.drop();
	}
}
