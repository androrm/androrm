/**
 * 	Copyright (c) 2012 Philipp Giese
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.orm.androrm.test.migration;

import java.util.ArrayList;
import java.util.List;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.Model;
import com.orm.androrm.field.BlobField;
import com.orm.androrm.field.BooleanField;
import com.orm.androrm.field.CharField;
import com.orm.androrm.field.DataField;
import com.orm.androrm.field.DateField;
import com.orm.androrm.field.DoubleField;
import com.orm.androrm.field.IntegerField;
import com.orm.androrm.field.LocationField;
import com.orm.androrm.impl.migration.EmptyModel;
import com.orm.androrm.migration.Migrator;

public class DataFieldMigrationTest extends AbstractMigrationTest {

	@Override
	public void setUp() {
		List<Class<? extends Model>> models = new ArrayList<Class<? extends Model>>();
		models.add(EmptyModel.class);
		
		DatabaseAdapter adapter = DatabaseAdapter.getInstance(getContext());
		adapter.setModels(models);
		
		super.setUp();
	}
	
	public void testCharFieldAdd() {
		fieldAdd("mChar", new CharField());
	}
	
	public void testIntegerFieldAdd() {
		fieldAdd("mInteger", new IntegerField());
	}
	
	public void testDoubleFieldAdd() {
		fieldAdd("mDouble", new DoubleField());
	}
	
	public void testBlobFieldAdd() {
		fieldAdd("mBlob", new BlobField());
	}
	
	public void testLocationFieldAdd() {
		Migrator<EmptyModel> migrator = new Migrator<EmptyModel>(EmptyModel.class);
		
		assertFalse(mHelper.hasField(EmptyModel.class, "mLocationLat"));
		assertFalse(mHelper.hasField(EmptyModel.class, "mLocationLng"));
		
		migrator.addField("mLocation", new LocationField());
		migrator.migrate(getContext());
		
		assertTrue(mHelper.hasField(EmptyModel.class, "mLocationLat"));
		assertTrue(mHelper.hasField(EmptyModel.class, "mLocationLng"));
		
	}
	
	public void testBooleanFieldAdd() {
		fieldAdd("mBoolean", new BooleanField());
	}
	
	public void testDateFieldAdd() {
		fieldAdd("mDate", new DateField());
	}
	
	private void fieldAdd(String name, DataField<?> field) {
		Migrator<EmptyModel> migrator = new Migrator<EmptyModel>(EmptyModel.class);
		
		assertFalse(mHelper.hasField(EmptyModel.class, name));
		
		migrator.addField(name, field);
		migrator.migrate(getContext());
		
		assertTrue(mHelper.hasField(EmptyModel.class, name));
	}
	
}
