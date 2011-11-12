package com.orm.androrm.impl;

import java.util.Collection;

import android.content.Context;

import com.orm.androrm.CharField;
import com.orm.androrm.Model;
import com.orm.androrm.OneToManyField;
import com.orm.androrm.QuerySet;

public class Brand extends Model {

	public static final QuerySet<Brand> objects(Context context) {
		return objects(context, Brand.class);
	}
	
	protected OneToManyField<Brand, Branch> mBranches;
	protected CharField mName;
	
	public Brand() {
		super();
		
		mBranches = new OneToManyField<Brand, Branch>(Brand.class, Branch.class);
		mName = new CharField();
	}
	
	public void addBranch(Branch branch) {
		mBranches.add(branch);
	}
	
	public void addBranches(Collection<Branch> branches) {
		mBranches.addAll(branches);
	}
	
	public QuerySet<Branch> getBranches(Context context) {
		return mBranches.get(context, this);
	}
	
	public int branchCount(Context context) {
		return mBranches.get(context, this).count();
	}
	
	public void setName(String name) {
		mName.set(name);
	}
	
	public String getName() {
		return mName.get();
	}
	
}
