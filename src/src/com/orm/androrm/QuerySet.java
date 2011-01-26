/**
 * 
 */
package com.orm.androrm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * @author Philipp Giese
 *
 */
public class QuerySet<T extends Model> implements Iterable<T> {

	private static final String TAG = "ANDRORM:QUERY:SET";
	
	private SelectStatement mQuery;
	private Class<T> mClass;
	private Context mContext;
	private List<T> mItems;
	
	public QuerySet(Context context, Class<T> model) {
		mContext = context;
		mClass = model;
	}
	
	public T get(int id) {
		Where where = new Where();
		where.setStatement(new Statement("mId", id));
		
		if(mQuery == null) {
			mQuery = new SelectStatement();
			mQuery.from(DatabaseBuilder.getTableName(mClass))
				  .where(where);
		} else {
			mQuery.where(where);
		}
		
		DatabaseAdapter adapter = new DatabaseAdapter(mContext);
		adapter.open();
		
		Cursor c = adapter.query(mQuery);
		
		T object = createObject(c);
		
		c.close();
		adapter.close();
		
		return object;
	}
	
	public QuerySet<T> orderBy(String... columns) {
		if(mQuery != null) {
			mQuery.orderBy(columns);
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
	
	public QuerySet<T> filter(Filter filter) {
		SelectStatement query = null;
		
		try {
			query = QueryBuilder.buildQuery(mClass, filter.getFilters(), 0);
		} catch (NoSuchFieldException e) {
			Log.e(TAG, "could not build query for filter", e);
		}
		
		if(mQuery == null) {
			mQuery = query;
		} else {
			JoinStatement join = new JoinStatement();
			join.left(mQuery, "left")
				.right(query, "right")
				.on("mId", "mId");
			
			SelectStatement select = new SelectStatement();
			select.from(join);
			
			mQuery = select;
		}
		
		return this;
	}
	
	public QuerySet<T> limit(int limit) {
		if(mQuery != null) {
			mQuery.limit(new Limit(limit));
		}
		
		return this;
	}
	
	public QuerySet<T> limit(int offset, int limit) {
		if(mQuery != null) {
			mQuery.limit(new Limit(offset, limit));
		}
		
		return this;
	}
	
	public QuerySet<T> limit(Limit limit) {
		if(mQuery != null) {
			mQuery.limit(limit);
		}
		
		return this;
	}
	
	public int count() {
		if(mQuery != null) {
			SelectStatement query = new SelectStatement();
			query.from(mQuery)
				 .count();
			
			DatabaseAdapter adapter = new DatabaseAdapter(mContext);
			adapter.open();
			
			Cursor c = adapter.query(query);
			
			int count = 0;
			
			if(c.moveToNext()) {
				count = c.getInt(c.getColumnIndexOrThrow(Model.COUNT));
			}
			
			c.close();
			adapter.close();
			
			return count;
		}
		
		return 0;
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
				DatabaseAdapter adapter = new DatabaseAdapter(mContext);
				adapter.open();
				
				Cursor c = adapter.query(mQuery);
				mItems.addAll(createObjects(c));
				
				c.close();
				adapter.close();
			}
		}
		
		return mItems;
	}
	
	@Override
	public Iterator<T> iterator() {
		return getItems().iterator();
	}

	public boolean contains(Object object) {
		return getItems().contains(object);
	}

	public boolean containsAll(Collection<?> arg0) {
		return getItems().containsAll(arg0);
	}

	public boolean isEmpty() {
		return getItems().isEmpty();
	}

}
