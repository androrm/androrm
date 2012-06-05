package com.orm.androrm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ModelCache {
	
	private static List<Class<? extends Model>> KNOWN_MODELS = new ArrayList<Class<? extends Model>>();
	
	private static Map<Class<? extends Model>, List<TableDefinition>> TABLE_DEFINITIONS = new HashMap<Class<? extends Model>, List<TableDefinition>>();
	
	private static Map<Class<? extends Model>, List<String>> KNOWN_MODEL_FIELDS = new HashMap<Class<? extends Model>, List<String>>();
	
	private static Map<Class<? extends Model>, List<Field>> KNOWN_FIELD_INSTANCES = new HashMap<Class<? extends Model>, List<Field>>();
	
	private static Map<String, Field> FIELD_SHORTCUTS = new HashMap<String, Field>();
	
	public static <T extends Model> boolean knowsModel(Class<T> clazz) {
		return KNOWN_MODELS.contains(clazz);
	}
	
	public static <T extends Model> boolean knowsFields(Class<T> clazz) {
		if(knowsModel(clazz)) {
			return !KNOWN_MODEL_FIELDS.get(clazz).isEmpty();
		}
		
		return false;
	}
	
	public static <T extends Model> void addModel(Class<T> clazz) {
		if(knowsModel(clazz)) {
			return;
		}
		
		KNOWN_MODELS.add(clazz);
		KNOWN_MODEL_FIELDS.put(clazz, new ArrayList<String>());
		KNOWN_MODEL_FIELDS.put(clazz, new ArrayList<String>());
	}
	
	public static <T extends Model> List<TableDefinition> getTableDefinitions(Class<T> clazz) {
		if(knowsModel(clazz)) {
			return TABLE_DEFINITIONS.get(clazz);
		}
		
		return null;
	}
	
	public static <T extends Model> void setTableDefinitions(Class<T> clazz, List<TableDefinition> definitions) {
		TABLE_DEFINITIONS.put(clazz, definitions);
	}
	
	public static <T extends Model> void setModelFields(Class<T> clazz, List<Field> fields) {
		if(knowsModel(clazz)) {		
			for(Field field : fields) {
				String fieldName = field.getName();
				
				KNOWN_MODEL_FIELDS.get(clazz).add(fieldName);
				FIELD_SHORTCUTS.put(clazz.toString() + fieldName, field);
			}
			
			KNOWN_FIELD_INSTANCES.put(clazz, fields);
		}
	}
	
	public static <T extends Model> List<Field> fieldsForModel(Class<T> clazz) {
		if(knowsModel(clazz)) {
			return KNOWN_FIELD_INSTANCES.get(clazz);
		}
		
		return new ArrayList<Field>();
	}
	
	public static <T extends Model> boolean modelHasField(Class<T> clazz, String field) {
		if(knowsFields(clazz)) {
			return KNOWN_MODEL_FIELDS.get(clazz).contains(field);
		}
		
		return false;
	}
	
	public static <T extends Model> Field getField(Class<T> clazz, String fieldName) {
		if(knowsFields(clazz)) {
			return FIELD_SHORTCUTS.get(clazz.toString() + fieldName);
		}
		
		return null;
	}
	
	public static void reset() {
		KNOWN_MODELS.clear();
		KNOWN_FIELD_INSTANCES.clear();
		KNOWN_MODEL_FIELDS.clear();
		TABLE_DEFINITIONS.clear();
		FIELD_SHORTCUTS.clear();
	}
}
