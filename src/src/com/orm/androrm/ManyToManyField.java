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
import java.util.Collections;
import java.util.HashSet;
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
extends AbstractToManyRelation<L, R> {

	private static final String TAG = "ANDRORM:M2M";
	
	private String mTableName;
	
	public ManyToManyField(Class<L> origin, 
			Class<R> target) {
		
		setUp(origin, target, false);
	}
	
	public ManyToManyField(Class<L> origin,
			Class<R> target,
			boolean isSet) {
		
		setUp(origin, target, isSet);
	}
	
	private void setUp(Class<L> origin, Class<R> target, boolean isSet) {
		mOriginClass = origin;
		mTargetClass = target;
		
		if(isSet) {
			mValues = new HashSet<R>();
		} else {
			mValues = new ArrayList<R>();
		}
		
		mTableName = createTableName();
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
	
	private String createTableName() {
		List<String> tableNames = new ArrayList<String>();
		tableNames.add(DatabaseBuilder.getTableName(mOriginClass));
		tableNames.add(DatabaseBuilder.getTableName(mTargetClass));
		
		Collections.sort(tableNames);
		
		return tableNames.get(0) + "_" + tableNames.get(1);
	}
	
	@Override
	public List<R> get(Context context, L l, Limit limit) {
		// TODO: take limit into account
		if(!mInvalidated 
				&& mValues.isEmpty()) {
			
			SelectStatement select = getQuery(l.getId(), limit).orderBy(mOrderBy);
			
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
		
		return new ArrayList<R>(mValues);
	}
	
	private JoinStatement getJoin(String leftAlias, String rightAlias, int id) {
		JoinStatement join = new JoinStatement();
		
		join.left(DatabaseBuilder.getTableName(mTargetClass), leftAlias)
			.right(getRightJoinSide(id), rightAlias)
			.on(Model.PK, DatabaseBuilder.getTableName(mTargetClass));
		
		return join;
	}
	
	public ForeignKeyField<L> getLeftLinkDescriptor() {
		return new ForeignKeyField<L>(mOriginClass);
	}
	
	private List<R> getObjects(Cursor c) {
		List<R> values = new ArrayList<R>();
		
		while(c.moveToNext()) {
			R object = Model.createObject(mTargetClass, c);
			
			values.add(object);
		}
		
		return values;
	}
	
	private SelectStatement getQuery(int id, Limit limit) {
		SelectStatement select = new SelectStatement();
		
		select.select("a.*")
		  	  .from(getJoin("a", "b", id))
		  	  .limit(limit);
		
		return select;
	}
	
	public String getRelationTableName() {
		return mTableName;
	}
	
	public ForeignKeyField<R> getRightHandDescriptor() {
		return new ForeignKeyField<R>(mTargetClass);
	}
	
	private SelectStatement getRightJoinSide(int id) {
		String leftTable = DatabaseBuilder.getTableName(mOriginClass);
		String rightTable = DatabaseBuilder.getTableName(mTargetClass);
		
		Where where = new Where();
		where.setStatement(new Statement(leftTable, id));
		
		SelectStatement relation = new SelectStatement();
		relation.from(mTableName)
				.select(leftTable, rightTable)
		 		.where(where);
		
		JoinStatement join = new JoinStatement();
		join.left(relation, "left")
			.right(rightTable, "right")
			.on(rightTable, Model.PK);
		
		SelectStatement select = new SelectStatement();
		select.from(join)
			  .select("left." + rightTable + " AS " + rightTable);
		
		return select;
	}
}
