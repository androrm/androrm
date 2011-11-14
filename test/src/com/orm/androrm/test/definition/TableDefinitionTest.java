package com.orm.androrm.test.definition;

import java.util.List;

import android.test.AndroidTestCase;

import com.orm.androrm.ForeignKeyField;
import com.orm.androrm.IntegerField;
import com.orm.androrm.Model;
import com.orm.androrm.TableDefinition;
import com.orm.androrm.impl.Product;

public class TableDefinitionTest extends AndroidTestCase {

	public void testTableName() {
		TableDefinition def = new TableDefinition("foo");
		
		assertEquals("foo", def.getTableName());
		assertEquals("CREATE TABLE IF NOT EXISTS foo ();", def.toString());
	}
	
	public void testAddSimpleField() {
		TableDefinition def = new TableDefinition("foo");
		IntegerField i = new IntegerField();
		
		def.addField("pk", i);

		assertEquals("CREATE TABLE IF NOT EXISTS foo (pk integer);", def.toString());
	}
	
	public void testForeignKeyField() {
		TableDefinition def = new TableDefinition("foo");
		ForeignKeyField<Product> fk = new ForeignKeyField<Product>(Product.class);
		
		def.addField("product", fk);
		
		assertEquals("CREATE TABLE IF NOT EXISTS foo ("
					+ "product integer," 
					+ "FOREIGN KEY (product) "
						+ "REFERENCES product (mId) "
						+ "ON DELETE CASCADE);", def.toString());
	}
	
	public void testRelationalClasses() {
		TableDefinition def = new TableDefinition("foo");
		
		def.addRelationalClass(Product.class);
		List<Class<? extends Model>> relations = def.getRelationalClasses();
		
		assertEquals(1, relations.size());
		assertTrue(relations.contains(Product.class));
	}
}
