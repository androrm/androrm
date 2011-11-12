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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	
	public void addField(String fieldName, DataField<?> field) {
		mFields.put(fieldName, field);
		
		if(field instanceof ForeignKeyField) {
			mRelations.put(fieldName, (ForeignKeyField<?>) field);
		}
	}
	
	public <T extends Model> void addRelationalClass(Class<T> clazz) {
		mRelationalClasses.add(clazz);
	}
	
	private <T extends DataField<?>> String getFieldDefintions(Map<String, T> fields, boolean addConstraints) {
		boolean first = true;
		
		String definition = "";
		
		for(Entry<String, T> entry : fields.entrySet()) {
			T value = entry.getValue();
			String part = value.getDefinition(entry.getKey());
			
			if(addConstraints 
					&& value instanceof ForeignKeyField) {
				
				ForeignKeyField<?> fk = (ForeignKeyField<?>) value;
				part = fk.getConstraint(entry.getKey());
			}
			
			if(first) {
				definition += part;
				first = false;
			} else {
				definition += "," + part;
			}
		}
		
		return definition;
	}
	
	public List<Class<? extends Model>> getRelationalClasses() {
		return mRelationalClasses;
	}
	
	public String getTableName() {
		return mTableName;
	}
	
	@Override
	public String toString() {
		
		String definition = getFieldDefintions(mFields, false);
		
		if(!mRelations.isEmpty()) {
			definition += ",";
			definition += getFieldDefintions(mRelations, true);
		}
		
		definition = "CREATE TABLE IF NOT EXISTS " + mTableName + " (" + definition + ");";
		
		return definition;
	}
}
