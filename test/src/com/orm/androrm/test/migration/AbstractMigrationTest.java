package com.orm.androrm.test.migration;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.migration.MigrationHelper;

import android.test.AndroidTestCase;

public abstract class AbstractMigrationTest extends AndroidTestCase {

	protected MigrationHelper mHelper;
	
	@Override
	public void setUp() {
		mHelper = new MigrationHelper(getContext());
	}
	
	@Override
	public void tearDown() {
		DatabaseAdapter adapter = DatabaseAdapter.getInstance(getContext());
		adapter.drop();
		adapter.resetMigrations();
	}
	
}
