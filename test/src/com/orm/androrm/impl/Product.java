package com.orm.androrm.impl;

import java.util.List;

import android.content.Context;

import com.orm.androrm.CharField;
import com.orm.androrm.ManyToManyField;
import com.orm.androrm.Model;
import com.orm.androrm.QuerySet;

public class Product extends Model {

	public static final QuerySet<Product> objects(Context context) {
		return objects(context, Product.class);
	}
	
	protected CharField mName;
	protected ManyToManyField<Product, Branch> mBranches;
	
	public Product() {
		super();
		
		mName = new CharField(50);
		mBranches = new ManyToManyField<Product, Branch>(Product.class, Branch.class);
	}

	public void setName(String name) {
		mName.set(name);
	}
	
	public String getName() {
		return mName.get();
	}
	
	public void addBranch(Branch branch) {
		mBranches.add(branch);
	}
	
	public void addBranches(List<Branch> branches) {
		mBranches.addAll(branches);
	}
	
	public QuerySet<Branch> getBranches(Context context) {
		return mBranches.get(context, this);
	}
	
	public int branchCount(Context context) {
		return mBranches.get(context, this).count();
	}
	
}
