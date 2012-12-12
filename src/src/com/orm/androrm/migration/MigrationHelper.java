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

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.database.Cursor;

import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.DatabaseBuilder;
import com.orm.androrm.Model;
import com.orm.androrm.field.ManyToManyField;

public class MigrationHelper {

	private DatabaseAdapter mAdapter;
	
	public MigrationHelper(Context context) {
		mAdapter = DatabaseAdapter.getInstance(context);
	}
	
	private Cursor getCursor(String query) {
		mAdapter.open();
		return mAdapter.query(query);
	}
	
	private void close(Cursor cursor) {
		cursor.close();
		mAdapter.close();
	}
	
	public boolean hasField(Class<? extends Model> model, String name) {
		String table = DatabaseBuilder.getTableName(model);
		String sql = "PRAGMA TABLE_INFO(`" + table + "`)";
		
		Cursor c = getCursor(sql);
		
		while(c.moveToNext()) {
			String fieldName = c.getString(c.getColumnIndexOrThrow("name"));
			
			if(fieldName.equals(name)) {
				close(c);
				return true;
			}
		}
		
		close(c);
		return false;
	}
	
	public boolean tableExists(Class<? extends Model> model) {
		return tableExists(DatabaseBuilder.getTableName(model));
	}
	
	/**
	 * Checks whether a given table name already exists in the database.
	 * 
	 * @param name Name of the table to look up.
	 * @return <code>true</code> if one exists <code>false</code> otherwise.
	 */
	public boolean tableExists(String name) {
		String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name LIKE '" + name + "'";
		
		Cursor c = getCursor(sql);
		
		while(c.moveToNext()) {
			String table = c.getString(c.getColumnIndexOrThrow("name"));
			
			if(table.equalsIgnoreCase(name)) {
				close(c);
				
				return true;
			}
		}
		
		close(c);
		return false;
	}
	
	/**
	 * Check if a given model has relational tables in the system. 
	 * Relational tables are created if a model has one or more 
	 * {@link ManyToManyField} instances. 
	 * 
	 * @param model The {@link Model} class to look up. 
	 * @return <code>true</code> if there are any, <code>false</code> otherwise.
	 */
	public boolean hasRelationTable(Class<? extends Model> model) {
		return hasRelationTable(DatabaseBuilder.getTableName(model));
	}
	
	/**
	 * Alias for {@link MigrationHelper#hasRelationTable(Class)}.
	 * @param name
	 * @return
	 */
	public boolean hasRelationTable(String name) {
		return !getRelationTableNames(name).isEmpty();
	}
	
	public List<String> getRelationTableNames(String table) {
		List<String> result = new ArrayList<String>();
		String sql = "SELECT name FROM sqlite_master WHERE type='table' AND (name LIKE '" + table + "#_%' OR name LIKE '%#_" + table +"' ESCAPE '#')";
		
		Cursor c = getCursor(sql);
		
		while(c.moveToNext()) {
			String name = c.getString(c.getColumnIndexOrThrow("name"));
		
			if(!name.equalsIgnoreCase(table) && (
					StringUtils.startsWithIgnoreCase(name, table) || 
					StringUtils.endsWithIgnoreCase(name, table)
				)) {
				
				result.add(name);
			}
		}
		
		close(c);
		return result;
	}
}
