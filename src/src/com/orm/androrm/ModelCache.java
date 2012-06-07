package com.orm.androrm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ModelCache {
	
	private static List<String> KNOWN_MODELS = new ArrayList<String>();
	
	private static Map<String, List<TableDefinition>> TABLE_DEFINITIONS = new HashMap<String, List<TableDefinition>>();
	
	private static Map<String, List<String>> KNOWN_MODEL_FIELDS = new HashMap<String, List<String>>();
	
	private static Map<String, List<Field>> KNOWN_FIELD_INSTANCES = new HashMap<String, List<Field>>();
	
	private static Map<String, Field> FIELD_SHORTCUTS = new HashMap<String, Field>();
	
	public static <T extends Model> boolean knowsModel(Class<T> clazz) {
		return knowsModel(DatabaseBuilder.getTableName(clazz));
	}
	
	public static boolean knowsModel(String modelName) {
		return KNOWN_MODELS.contains(modelName);
	}
	
	public static <T extends Model> boolean knowsFields(Class<T> clazz) {
		if(knowsModel(clazz)) {
			return !KNOWN_MODEL_FIELDS.get(DatabaseBuilder.getTableName(clazz)).isEmpty();
		}
		
		return false;
	}
	
	public static <T extends Model> void addModel(Class<T> clazz) {
		String modelName = DatabaseBuilder.getTableName(clazz);
		
		if(knowsModel(modelName)) {
			return;
		}
		
		KNOWN_MODELS.add(modelName);
		KNOWN_MODEL_FIELDS.put(modelName, new ArrayList<String>());
		KNOWN_MODEL_FIELDS.put(modelName, new ArrayList<String>());
	}
	
	public static <T extends Model> List<TableDefinition> getTableDefinitions(Class<T> clazz) {
		if(knowsModel(clazz)) {
			return TABLE_DEFINITIONS.get(DatabaseBuilder.getTableName(clazz));
		}
		
		return null;
	}
	
	public static <T extends Model> void setTableDefinitions(Class<T> clazz, List<TableDefinition> definitions) {
		TABLE_DEFINITIONS.put(DatabaseBuilder.getTableName(clazz), definitions);
	}
	
	public static <T extends Model> void setModelFields(Class<T> clazz, List<Field> fields) {
		if(knowsModel(clazz)) {
			String modelName = DatabaseBuilder.getTableName(clazz);
			
			for(Field field : fields) {
				String fieldName = field.getName();
				
				KNOWN_MODEL_FIELDS.get(modelName).add(fieldName);
				FIELD_SHORTCUTS.put(modelName + fieldName, field);
			}
			
			KNOWN_FIELD_INSTANCES.put(modelName, fields);
		}
	}
	
	public static <T extends Model> List<Field> fieldsForModel(Class<T> clazz) {
		if(knowsModel(clazz)) {
			return KNOWN_FIELD_INSTANCES.get(DatabaseBuilder.getTableName(clazz));
		}
		
		return new ArrayList<Field>();
	}
	
	public static <T extends Model> boolean modelHasField(Class<T> clazz, String field) {
		if(knowsFields(clazz)) {
			return KNOWN_MODEL_FIELDS.get(DatabaseBuilder.getTableName(clazz)).contains(field);
		}
		
		return false;
	}
	
	public static <T extends Model> Field getField(Class<T> clazz, String fieldName) {
		if(knowsFields(clazz)) {
			return FIELD_SHORTCUTS.get(DatabaseBuilder.getTableName(clazz) + fieldName);
		}
		
		return null;
	}
	
	public static void reset(String model) {
		KNOWN_MODELS.remove(model);
		KNOWN_FIELD_INSTANCES.remove(model);
		KNOWN_MODEL_FIELDS.remove(model);
		TABLE_DEFINITIONS.remove(model);
		FIELD_SHORTCUTS.remove(model);
	}
	
	public static void reset() {
		KNOWN_MODELS.clear();
		KNOWN_FIELD_INSTANCES.clear();
		KNOWN_MODEL_FIELDS.clear();
		TABLE_DEFINITIONS.clear();
		FIELD_SHORTCUTS.clear();
	}
}
