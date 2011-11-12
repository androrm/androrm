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
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

/**
 * @author Philipp Giese
 */
public class QuerySet<T extends Model> implements Iterable<T> {

	private SelectStatement mQuery;
	private Class<T> mClass;
	private List<T> mItems;
	private DatabaseAdapter mAdapter;
	
	public QuerySet(Context context, Class<T> model) {
		mClass = model;
		mAdapter = new DatabaseAdapter(context);
	}
	
	protected void injectQuery(SelectStatement query) {
		mQuery = query;
	}
	
	private Cursor getCursor(SelectStatement query) {
		mAdapter.open();
		return mAdapter.query(query);
	}
	
	private void closeConnection(Cursor c) {
		c.close();
		mAdapter.close();
	}
	
	public T get(int id) {
		Where where = new Where();
		where.setStatement(new Statement(Model.PK, id));
		
		if(mQuery == null) {
			mQuery = new SelectStatement();
			mQuery.from(DatabaseBuilder.getTableName(mClass));
		}
		
		mQuery.where(where);
		
		Cursor c = getCursor(mQuery);
		T object = createObject(c);
		closeConnection(c);
		
		return object;
	}
	
	public QuerySet<T> orderBy(String... columns) {
		if(mQuery != null) {
			SelectStatement query = new SelectStatement();
			query.from(mQuery)
				 .orderBy(columns);

			mQuery = query;
		}
		
		return this;
	}
	
	public QuerySet<T> distinct() {
		if(mQuery != null) {
			mQuery.distinct();
		}
		
		return this;
	}

	public QuerySet<T> all() {
		if(mQuery == null) {
			mQuery = new SelectStatement();
			mQuery.from(DatabaseBuilder.getTableName(mClass));
		}
		
		return this;
	}
	
	public QuerySet<T> filter(Filter filter) throws NoSuchFieldException {
		SelectStatement query = QueryBuilder.buildQuery(mClass, filter.getRules());
		
		if(mQuery == null) {
			mQuery = query;
		} else {
			JoinStatement join = new JoinStatement();
			join.left(mQuery, "left")
				.right(query, "right")
				.on(Model.PK, Model.PK);
			
			SelectStatement select = new SelectStatement();
			select.from(join);
			
			mQuery = select;
		}
		
		return this;
	}
	
	public QuerySet<T> limit(int limit) {
		return limit(new Limit(limit));
	}
	
	public QuerySet<T> limit(int offset, int limit) {
		return limit(new Limit(offset, limit));
	}
	
	public QuerySet<T> limit(Limit limit) {
		if(mQuery != null) {
			mQuery.limit(limit);
		}
		
		return this;
	}
	
	public int count() {
		if(mQuery != null) {
			return getCount(mQuery);
		}
		
		return all().count();
	}
	
	private T createObject(Cursor c) {
		T object = null;
		
		if(c.moveToNext()) {
			object = Model.createObject(mClass, c);
		}
		
		return object;
	}
	
	private List<T> createObjects(Cursor c) {
		List<T> items = new ArrayList<T>();
		
		while(c.moveToNext()) {
			T object = Model.createObject(mClass, c);
			
			if(object != null) {
				items.add(object);
			}
		}
		
		return items;
	}
	
	private List<T> getItems() {
		if(mItems == null) {
			mItems = new ArrayList<T>();
			
			if(mQuery != null) {
				Cursor c = getCursor(mQuery);
				mItems.addAll(createObjects(c));
				closeConnection(c);
			}
		}
		
		return mItems;
	}
	
	@Override
	public Iterator<T> iterator() {
		return getItems().iterator();
	}
	
	private int getCount(SelectStatement query) {
		SelectStatement countQuery = new SelectStatement();
		countQuery.from(query)
			 	  .count();
		
		Cursor c = getCursor(countQuery);
		
		int count = 0;
		
		if(c.moveToFirst()) {
			count = c.getInt(c.getColumnIndexOrThrow(Model.COUNT));
		}
		
		closeConnection(c);
		
		return count;
	}

	/**
	 * Checks if the result of this query contains the given 
	 * object. Note, that this operation will execute the query
	 * on the database. Use only, if you have to. 
	 * 
	 * @param value
	 * @return
	 */
	public boolean contains(T value) {
		if(mQuery != null) {
			Where where = new Where();
			where.setStatement(new Statement(Model.PK, value.getId()));
			
			SelectStatement query = new SelectStatement();
			query.from(mQuery)
				 .where(where);
			
			return getCount(query) != 0;
		}
		
		return false;
	}

	/**
	 * See {@link QuerySet#contains}
	 * @param values
	 * @return
	 */
	public boolean containsAll(Collection<T> values) {
		if(mQuery != null) {
			List<Integer> ids = new ArrayList<Integer>();
			
			for(T item : values) {
				ids.add(item.getId());
			}
			
			Where where = new Where();
			where.setStatement(new InStatement(Model.PK, ids));
			
			SelectStatement query = new SelectStatement();
			query.from(mQuery)
				 .where(where);
			
			return getCount(query) == values.size();
		}
		
		return false;
	}

	public boolean isEmpty() {
		return count() == 0;
	}

	public List<T> toList() {
		return getItems();
	}
}
