package com.orm.androrm.test.field;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.test.AndroidTestCase;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.DateField;
import com.orm.androrm.Model;
import com.orm.androrm.impl.BlankModel;

public class DateFieldTest extends AndroidTestCase {

	@Override
	public void setUp() {
		List<Class<? extends Model>> models = new ArrayList<Class<? extends Model>>();
		models.add(BlankModel.class);

		DatabaseAdapter.setDatabaseName("test_db");

		DatabaseAdapter adapter = new DatabaseAdapter(getContext());
		adapter.setModels(models);
	}

	@Override
	public void tearDown() {
		DatabaseAdapter adapter = new DatabaseAdapter(getContext());
		adapter.drop();
	}

	public void testDefaults() {
		DateField d = new DateField();

		assertEquals("foo varchar(19)", d.getDefinition("foo"));
		assertNull(d.get());
	}

	public void testDateFromString() {
		DateField d = new DateField();
		String date = "2010-11-02T12:23:43";

		d.fromString(date);

		Date time = d.get();

		assertEquals(2010 - 1900, time.getYear());
		// somehow months start at 0
		assertEquals(11 - 1, time.getMonth());
		assertEquals(2, time.getDay());
		assertEquals(12, time.getHours());
		assertEquals(23, time.getMinutes());
		assertEquals(43, time.getSeconds());

		date = "sadjsdnksjdnf";

		d = new DateField();
		d.fromString(date);

		assertNull(d.get());
	}

	public void testGetDateString() {
		DateField d = new DateField();
		String date = "2010-11-02T12:23:43";
		d.fromString(date);

		assertEquals(date, d.getDateString());
	}

	public void testSetAndGet() {
		DateField d = new DateField();
		Calendar c = Calendar.getInstance();
		Date date = c.getTime();

		d.set(date);

		assertEquals(date, d.get());
	}

	public void testStorage() {
		Date date = Calendar.getInstance().getTime();
		BlankModel model = new BlankModel();
		model.setDate(date);
		model.save(getContext());

		assertEquals(date, model.getDate());

		model = Model.objects(getContext(), BlankModel.class).get(model.getId());
		Date newDate = model.getDate();

		assertNotNull(newDate);

		assertEquals(date.getYear(), newDate.getYear());
		assertEquals(date.getMonth(), newDate.getMonth());
		assertEquals(date.getDay(), newDate.getDay());
		assertEquals(date.getHours(), newDate.getHours());
		assertEquals(date.getMinutes(), newDate.getMinutes());
		assertEquals(date.getSeconds(), newDate.getSeconds());
	}
}
