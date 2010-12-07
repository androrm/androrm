/**
 * 
 */
package com.orm.androrm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.util.Log;

/**
 * @author Philipp Giese
 *
 */
public class OneToManyField<L extends Model, R extends Model> implements Relation {

	private static final String TAG = "ANDRORM:O2M";
	
	private Set<R> mValues;
	private Class<R> mTargetClass;
	private Class<L> mOriginClass;
	
	public OneToManyField(Class<L> origin, Class<R> target) {
		mOriginClass = origin;
		mTargetClass = target;
		mValues = new HashSet<R>();
	}
	
	@Override
	public Class<? extends Model> getTarget() {
		return mTargetClass;
	}
	
	public void add(L target, R value) {
		try {
			Model.setBackLink(target, mOriginClass, value, mTargetClass);
			mValues.add(value);
		} catch (NoSuchFieldException e) {
			Log.e(TAG, "could not resolve one to many field into class " 
					+ mTargetClass.getSimpleName(), e);
		}
	}
	
	public List<R> get(Context context, L l) {
		String fieldName = Model.getBackLinkFieldName(context, mOriginClass, mTargetClass);
		
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(fieldName, l);
		
		List<R> result = Model.filter(context, mTargetClass, filter);
		
		mValues.addAll(result);
		
		return new ArrayList<R>(mValues);
	}

}
