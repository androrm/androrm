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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/**
 * @author Philipp Giese
 *
 */
public class DatabaseBuilder {

	private static final String TAG = "ANDORM:DATABASE:BUILDER";
	
	public static final String getTableName(Class<?> clazz) {
		return clazz.getSimpleName().toLowerCase();
	}
	
	protected static final<T extends Model> List<TableDefinition> getTableDefinitions(Class<T> clazz) {
		List<TableDefinition> definitions = new ArrayList<TableDefinition>();
		
		if(!Modifier.isAbstract(clazz.getModifiers())) {
			try {
				T object = Model.getInstace(clazz);
				TableDefinition definition = new TableDefinition(getTableName(clazz));
				
				getFieldDefinitions(object, clazz, definition);
				
				definitions.add(definition);
				
				for(Class<? extends Model> c: definition.getRelationalClasses()) {
					definitions.addAll(getRelationDefinitions(c));
				}
				
				return definitions;
			} catch(IllegalAccessException e) {
				Log.e(TAG, "an exception has been thrown while gathering the database structure information.", e);
			}
		}
		
		return null;
	}
	
	private static final<T extends Model> void getFieldDefinitions(
			
			T 				instance, 
			Class<T> 		clazz, 
			TableDefinition modelTable
			
	) throws IllegalArgumentException, IllegalAccessException {
		
		if(clazz != null && clazz.isInstance(instance)) {
			// TODO: only create fields from superclass, if superclass is
			// abstract. Otherwise create a pointer to superclass.
			
			for(Field field: getFields(clazz, instance)) {
				String name = field.getName();

				Object o = field.get(instance);
				
				if(o instanceof DataField) {
					DataField<?> fieldObject = (DataField<?>) o;
					modelTable.addField(name, fieldObject);
				}
				
				if(o instanceof ManyToManyField) {
					modelTable.addRelationalClass(clazz);
				}
			}
			
			getFieldDefinitions(instance, Model.getSuperclass(clazz), modelTable);
		}
	}
	
	/**
	 * Retrieves all fields of a given class, that are
	 * <ol>
	 * 	<li><b>NOT</b> private</li>
	 * 	<li>Database fields</li>
	 * </ol>
	 * In addition these fields are set to be accessible, so
	 * that they can then be further processed. 
	 * 
	 * @param clazz		Class to extract the fields from. 
	 * @param instance	Instance of that class. 
	 * 
	 * @return {@link List} of all fields, that are database fields, 
	 * 		   and that are <b>NOT</b> private. 
	 */
	protected static final List<Field> getFields(
			
			Class<? extends Model> 	clazz, 
			Model 					instance
			
	) {
		
		Field[] declaredFields = clazz.getDeclaredFields();
		List<Field> fields = new ArrayList<Field>();
		
		try {
			for(int i = 0, length = declaredFields.length; i < length; i++) {
				Field field = declaredFields[i];
				
				if(!Modifier.isPrivate(field.getModifiers())) {
					field.setAccessible(true);
					Object f = field.get(instance);
					
					if(isDatabaseField(f)) {
						fields.add(field);
					}
				}
			}
		} catch (IllegalAccessException e) {
			Log.e(TAG, "exception thrown while trying to gain access to fields of class " 
					+ clazz.getSimpleName(), e);
		}
		
		return fields;
	}
	
	private static final boolean isDatabaseField(Object field) {
		if(field != null) {
			if(field instanceof DataField
					|| isRelationalField(field)) {
				
				return true;
			}
		}
		
		return false;
	}
	
	protected static final boolean isRelationalField(Object field) {
		if(field != null) {
			if(field instanceof ForeignKeyField
					|| field instanceof OneToManyField
					|| field instanceof ManyToManyField) {
				
				return true;
			}
		}
		
		return false;
	}
	
	private static final<T extends Model> List<TableDefinition> getRelationDefinitions(Class<T> clazz) {
		List<TableDefinition> definitions = new ArrayList<TableDefinition>();
		
		T object = Model.getInstace(clazz);
		getRelationDefinitions(object, clazz, definitions);
		
		return definitions;
	}
	
	@SuppressWarnings("unchecked")
	private static final <T extends Model> void getRelationDefinitions(
			
			T 						instance, 
			Class<T> 				clazz, 
			List<TableDefinition> 	definitions
			
	) {
		
		if(clazz != null && clazz.isInstance(instance) ) {
			for(Field field: getFields(clazz, instance)) {
				try {
					Object o = field.get(instance);
					
					if(o instanceof ManyToManyField) {
						ManyToManyField<T, ?> m = (ManyToManyField<T, ?>) o;

						String leftHand = getTableName(clazz);
						String rightHand = getTableName(m.getTarget());
						
						TableDefinition definition = new TableDefinition(m.getRelationTableName());
						
						ForeignKeyField<T> leftLink = m.getLeftLinkDescriptor();
						ForeignKeyField<?> rightLink = m.getRightHandDescriptor();
						
						definition.addField(leftHand, leftLink);
						definition.addField(rightHand, rightLink);
						
						definitions.add(definition);
					}
				} catch(IllegalAccessException e) {
					Log.e(TAG, "could not gather relation definitions for class " 
							+ clazz.getSimpleName(), e);
				}
			}
			
			getRelationDefinitions(instance, Model.getSuperclass(clazz), definitions);
		}
	}
}
