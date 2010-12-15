package com.orm.androrm.impl;

import java.util.List;

import android.content.Context;

import com.orm.androrm.CharField;
import com.orm.androrm.FilterSet;
import com.orm.androrm.Model;
import com.orm.androrm.OneToManyField;

public class Product extends Model {

	public static final Product get(Context context, int id) {
		return get(context, Product.class, id);
	}
	
	public static final List<Product> filter(Context context, 
			FilterSet filter) {
		
		return filter(context, Product.class, filter);
	}
	
	protected CharField mName;
	protected OneToManyField<Product, Branch> mBranches;
	
	public Product() {
		super();
		
		mName = new CharField(50);
		mBranches = new OneToManyField<Product, Branch>(Product.class, Branch.class);
	}

	public void setName(String name) {
		mName.set(name);
	}
	
	public String getName() {
		return mName.get();
	}
	
	public void addBranch(Branch branch) {
		mBranches.add(this, branch);
	}
	
	public void addBranches(List<Branch> branches) {
		mBranches.addAll(this, branches);
	}
	
	public List<Branch> getBranches(Context context) {
		return mBranches.get(context, this);
	}
	
	public int branchCount(Context context) {
		return mBranches.count(context, this);
	}
	
}
