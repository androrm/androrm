package com.orm.androrm;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;



public class ManyToManyField<L extends Model, R extends Model> implements Relation {

	private static final String TAG = "MMR";
	
	private List<R> mValues;
	private Class<L> mOriginClass;
	private Class<R> mTargetClass;
	private String mTableName;
	
	public ManyToManyField(Class<L> origin, 
			Class<R> target) {
		
		mOriginClass = origin;
		mTargetClass = target;
		mValues = new ArrayList<R>();
		mTableName = createTableName();
	}
	
	private String createTableName() {
		List<String> tableNames = new ArrayList<String>();
		tableNames.add(Model.getTableName(mOriginClass));
		tableNames.add(Model.getTableName(mTargetClass));
		
		Collections.sort(tableNames);
		
		return tableNames.get(0) + "_" + tableNames.get(1);
	}
	
	@Override
	public Class<? extends Model> getTarget() {
		return mTargetClass;
	}
	
	public ForeignKeyField<L> getLeftLinkDescriptor() {
		return new ForeignKeyField<L>(mOriginClass);
	}
	
	public ForeignKeyField<R> getRightHandDescriptor() {
		return new ForeignKeyField<R>(mTargetClass);
	}
	
	public String getRelationTableName() {
		return mTableName;
	}

	public void add(R value) {
		if(value != null) {
			mValues.add(value);
		}
	}
	
	public void addAll(List<R> values) {
		if(values != null) {
			mValues.addAll(values);
		}
	}
	
	private SelectStatement getLeftJoinSide(int id) {
		SelectStatement left = new SelectStatement();
		
		Where leftWhere = new Where();
		leftWhere.setStatement(new Statement(Model.getTableName(mOriginClass), id));
		
		left.select(new String[] {Model.getTableName(mTargetClass)})
			.from(mTableName)
			.where(leftWhere);
		
		return left;
	}
	
	private SelectStatement getRightJoinSide() {
		SelectStatement right = new SelectStatement();
		
		right.from(Model.getTableName(mTargetClass));
		
		return right;
	}
	
	private JoinStatement getJoin(String leftAlias, String rightAlias, int id) {
		JoinStatement join = new JoinStatement();
		
		join.left(getLeftJoinSide(id), leftAlias)
			.right(getRightJoinSide(), rightAlias)
			.on(Model.getTableName(mTargetClass), Model.PK);
		
		return join;
	}
	
	private SelectStatement getQuery(int id) {
		SelectStatement select = new SelectStatement();
		
		select.select(new String[] {"b.*"})
		  	  .from(getJoin("a", "b", id));
		
		return select;
	}
	
	private List<R> getObjects(Context context, Cursor c) 
	throws SecurityException, 
		NoSuchMethodException, 
		IllegalArgumentException, 
		InstantiationException, 
		IllegalAccessException, 
		InvocationTargetException {
		
		List<R> values = new ArrayList<R>();
		
		Constructor<R> constructor = mTargetClass.getConstructor(Context.class);
		
		while(c.moveToNext()) {
			R object = constructor.newInstance(context);
			
			Model.createObject(object, mTargetClass, c);
			values.add(object);
		}
		
		return values;
	}
	
	public List<R> get(Context context, L l) {
		if(mValues.isEmpty()) {
			SelectStatement select = getQuery(l.getId());
			
			DatabaseAdapter adapter = new DatabaseAdapter(context);
			adapter.open();
			
			List<R> values = new ArrayList<R>();
			Cursor c = adapter.query(select.toString());
			
			try {
				values = getObjects(context, c);
			} catch(Exception e) {
				Log.e(TAG, "an error occurred creating objects for " 
						+ mTargetClass.getSimpleName(), e);
			} finally {
				c.close();
				adapter.close();
			}
			
			mValues = values;
		}
		
		return mValues;
	}

}
