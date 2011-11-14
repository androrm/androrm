package com.orm.androrm.test.field;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;
import android.location.LocationManager;
import android.test.AndroidTestCase;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.LocationField;
import com.orm.androrm.Model;
import com.orm.androrm.impl.BlankModel;

public class LocationFieldTest extends AndroidTestCase {

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

	public void testDefinition() {
		LocationField field = new LocationField();

		assertEquals("fooLat numeric, fooLng numeric", field.getDefinition("foo"));
	}

	public void testSave() {
		Location l = new Location(LocationManager.GPS_PROVIDER);
		l.setLatitude(1.0);
		l.setLongitude(2.0);

		BlankModel b = new BlankModel();
		b.setLocation(l);

		b.save(getContext());

		BlankModel b2 = Model.objects(getContext(), BlankModel.class).get(b.getId());
		Location l2 = b2.getLocation();

		assertEquals(1.0, l2.getLatitude());
		assertEquals(2.0, l2.getLongitude());
	}
}
