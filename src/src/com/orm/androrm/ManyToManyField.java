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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * @author Philipp Giese
 *
 * @param <L>	Type of the origin class.
 * @param <R>	Type of the target class.
 */
public class ManyToManyField<L extends Model, 
							 R extends Model> 
implements XToManyRelation<L, R> {

	private static final String TAG = "ANDRORM:M2M";
	
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
	public Class<R> getTarget() {
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
	
	@Override
	public void add(R value) {
		if(value != null) {
			mValues.add(value);
		}
	}
	
	@Override
	public void addAll(Collection<R> values) {
		if(values != null) {
			mValues.addAll(values);
		}
	}
	
	private SelectStatement getLeftJoinSide(int id) {
		SelectStatement left = new SelectStatement();
		
		left.from(Model.getTableName(mTargetClass));
		
		return left;
	}
	
	private SelectStatement getRightJoinSide(int id) {
		Where where = new Where();
		where.setStatement(new Statement(Model.getTableName(mOriginClass), id));
		
		SelectStatement relation = new SelectStatement();
		relation.from(mTableName)
				.select(new String[] {
						Model.getTableName(mOriginClass), 
						Model.getTableName(mTargetClass)
				})
		 		.where(where);
		
		JoinStatement join = new JoinStatement();
		join.left(relation, "left")
			.right(Model.getTableName(mTargetClass), "right")
			.on(Model.getTableName(mTargetClass), Model.PK);
		
		SelectStatement select = new SelectStatement();
		select.from(join)
			  .select(new String[] {"left." + Model.getTableName(mTargetClass) + " AS " + Model.getTableName(mTargetClass)});
		
		return select;
	}
	
	private JoinStatement getJoin(String leftAlias, String rightAlias, int id) {
		JoinStatement join = new JoinStatement();
		
		join.left(getLeftJoinSide(id), leftAlias)
			.right(getRightJoinSide(id), rightAlias)
			.on(Model.PK, Model.getTableName(mTargetClass));
		
		return join;
	}
	
	private SelectStatement getQuery(int id, Limit limit) {
		SelectStatement select = new SelectStatement();
		
		select.select(new String[] {"a.*"})
		  	  .from(getJoin("a", "b", id))
		  	  .limit(limit);
		
		return select;
	}
	
	private List<R> getObjects(Cursor c) {
		List<R> values = new ArrayList<R>();
		
		while(c.moveToNext()) {
			R object = Model.createObject(mTargetClass, c);
			
			values.add(object);
		}
		
		return values;
	}
	
	@Override
	public List<R> get(Context context, L l) {
		return get(context, l, null);
	}
	
	@Override
	public List<R> get(Context context, L l, Limit limit) {
		if(mValues.isEmpty()) {
			SelectStatement select = getQuery(l.getId(), limit);
			
			DatabaseAdapter adapter = new DatabaseAdapter(context);
			adapter.open();
			
			List<R> values = new ArrayList<R>();
			Cursor c = adapter.query(select);
			
			try {
				values = getObjects(c);
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

	@Override
	public int count(Context context, L origin) {
		if(origin.getId() != 0) {
			SelectStatement select = new SelectStatement();
			select.from(getJoin("a", "b", origin.getId()))
				  .distinct()
				  .count();
			
			DatabaseAdapter adapter = new DatabaseAdapter(context);
			adapter.open();
			
			Cursor c = adapter.query(select);
			int count = 0;
			if(c.moveToNext()) {
				count = c.getInt(c.getColumnIndexOrThrow("count"));
			}
			
			c.close();
			adapter.close();
			return count;
		}
		
		return mValues.size();
	}

}
