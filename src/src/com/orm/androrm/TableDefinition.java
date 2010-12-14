/**
 * 	Copyright (c) 2010 Philipp Giese
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
package com.orm.androrm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TableDefinition {
	private String mTableName;
	private Map<String, DataField<?>> mFields;
	private Map<String, ForeignKeyField<? extends Model>> mRelations;
	private List<Class<? extends Model>> mRelationalClasses;
	
	public TableDefinition(String tableName) {
		mFields = new HashMap<String, DataField<?>>();
		mRelations = new HashMap<String, ForeignKeyField<? extends Model>>();
		mTableName = tableName;
		mRelationalClasses = new ArrayList<Class<? extends Model>>();
	}
	
	public String getTableName() {
		return mTableName;
	}
	
	public void addField(String fieldName, DataField<?> field) {
		mFields.put(fieldName, field);
		
		if(field instanceof ForeignKeyField) {
			mRelations.put(fieldName, (ForeignKeyField<?>) field);
		}
	}
	
	public <T extends Model> void addRelationalClass(Class<T> clazz) {
		mRelationalClasses.add(clazz);
	}
	
	public List<Class<? extends Model>> getRelationalClasses() {
		return mRelationalClasses;
	}
	
	private <T extends DataField<?>> String getFieldDefintions(Map<String, T> fields) {
		boolean first = true;
		
		Set<Entry<String, T>> entries = fields.entrySet();
		Iterator<Entry<String, T>> iterator = entries.iterator();
		String definition = "";
		
		while(iterator.hasNext()) {
			Entry<String, T> entry = iterator.next();
			
			String part = entry.getValue().getDefinition(entry.getKey());
			
			if(first) {
				definition += part;
				first = false;
			} else {
				definition += "," + part;
			}
		}
		
		return definition;
	}
	
	private String getForeignKeys(Map<String, 
			ForeignKeyField<? extends Model>> keys) {
		
		boolean first = true;
		
		Set<Entry<String, ForeignKeyField<? extends Model>>> entries = keys.entrySet();
		Iterator<Entry<String, ForeignKeyField<? extends Model>>> iterator = entries.iterator();
		String definition = "";
		
		while(iterator.hasNext()) {
			Entry<String, ForeignKeyField<? extends Model>> entry = iterator.next();
			
			String part = entry.getValue().getConstraint(entry.getKey());
			
			if(first) {
				definition += part;
				first = false;
			} else {
				definition += ", " + part;
			}
		}
		
		return definition;
	}
	
	public String toString() {
		
		String definition = getFieldDefintions(mFields);
		
		if(!mRelations.isEmpty()) {
			definition += ",";
			definition += getForeignKeys(mRelations);
		}
		
		definition = "CREATE TABLE IF NOT EXISTS " + mTableName + " (" + definition + ");";
		
		return definition;
	}
}
