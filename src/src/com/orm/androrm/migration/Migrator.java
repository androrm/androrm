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
package com.orm.androrm.migration;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.orm.androrm.Model;
import com.orm.androrm.field.DatabaseField;
import com.orm.androrm.field.ManyToManyField;

/**
 * @author Philipp Giese
 *
 */
public class Migrator<T extends Model> {

	private Class<T> mModel;
	private List<Migratable<T>> mMigrations;
	
	public Migrator(Class<T> model) {
		mModel = model;
		mMigrations = new ArrayList<Migratable<T>>();
	}
	
	public void addField(String name, DatabaseField<?> field) {
		AddFieldMigration<T> migration = new AddFieldMigration<T>(name, field);
		
		mMigrations.add(migration);
	}
	
	public <R extends Model> void addField(String name, ManyToManyField<T, R> field) {
		// Tables for M2M relations are created anyway. This stub is only here,
		// so that users aren't confused, if they try to add a M2M field. We let
		// them believe "they" did it :)
		return;
	}
	
	public void renameModel(String old, Class<? extends Model> updated) {
		RenameModelMigration<T> migration = new RenameModelMigration<T>(old);
		
		mMigrations.add(migration);
	}
	
	public void renameRelation(String old, Class<? extends Model> updated) {
		RenameRelationMigration<T> migration = new RenameRelationMigration<T>(old);
		
		mMigrations.add(migration);
	}
	
	public void migrate(Context context) {
		for(Migratable<T> migration : mMigrations) {
			if(migration.execute(context, mModel)) {
				Migration.create(mModel, migration).save(context);
			}
		}
	}
	
}
