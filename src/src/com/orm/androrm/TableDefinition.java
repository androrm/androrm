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
	
	@SuppressWarnings("unchecked")
	public void addField(String fieldName, DataField<?> field) {
		mFields.put(fieldName, field);
		
		if(field instanceof ForeignKeyField) {
			mRelations.put(fieldName, (ForeignKeyField<? extends Model>) field);
		}
	}
	
	public void addRelationalClass(Class<? extends Model> clazz) {
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
