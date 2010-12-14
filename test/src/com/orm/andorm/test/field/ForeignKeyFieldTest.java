package com.orm.andorm.test.field;

import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.ForeignKeyField;
import com.orm.androrm.Model;
import com.orm.androrm.QueryBuilder;
import com.orm.androrm.impl.Branch;
import com.orm.androrm.impl.Product;

public class ForeignKeyFieldTest extends AndroidTestCase {

	@Override
	public void setUp() {
		List<Class<? extends Model>> models = new ArrayList<Class<? extends Model>>();
		models.add(Product.class);
		models.add(Branch.class);
		
		DatabaseAdapter.setDatabaseName("test_db");
		
		DatabaseAdapter adapter = new DatabaseAdapter(getContext());
		adapter.setModels(models);
	}
	
	public void testGetDefauls() {
		ForeignKeyField<Product> fk = new ForeignKeyField<Product>(Product.class);
		String targetTable = Model.getTableName(Product.class);
		
		assertEquals("product_id integer", fk.getDefinition("product_id"));
		assertEquals("FOREIGN KEY (product_id) REFERENCES " 
					+ targetTable 
					+ " (" 
					+ QueryBuilder.PK 
					+ ") ON DELETE CASCADE", fk.getConstraint("product_id"));
		
		assertNull(fk.get(getContext()));
	}
	
	public void testIsPersisted() {
		ForeignKeyField<Product> fk = new ForeignKeyField<Product>(Product.class);
		Product p = new Product();
		
		assertFalse(fk.isPersisted());
		
		fk.set(p);
		
		assertFalse(fk.isPersisted());
		p.save(getContext());
		
		assertTrue(fk.isPersisted());
	}
	
	public void testGetTarget() {
		ForeignKeyField<Product> fk = new ForeignKeyField<Product>(Product.class);
		
		assertEquals(Product.class, fk.getTarget());
	}
	
	public void testSetAndGet() {
		Product p = new Product();
		p.setName("test product");
		
		ForeignKeyField<Product> fk = new ForeignKeyField<Product>(Product.class);
		
		fk.set(p);
		assertEquals(p.getName(), fk.get(getContext()).getName());
		
		p.save(getContext());
		
		fk = new ForeignKeyField<Product>(Product.class);
		fk.set(p.getId());
		assertEquals(p.getName(), fk.get(getContext()).getName());
	}
	
	@Override
	public void tearDown() {
		DatabaseAdapter adapter = new DatabaseAdapter(getContext());
		adapter.drop();
	}
}
