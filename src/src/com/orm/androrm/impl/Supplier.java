package com.orm.androrm.impl;

import java.util.List;

import android.content.Context;

import com.orm.androrm.CharField;
import com.orm.androrm.ManyToManyField;
import com.orm.androrm.Model;
import com.orm.androrm.OneToManyField;

public class Supplier extends Model {

	protected CharField mName;
	protected ManyToManyField<Supplier, Product> mProducts;
	protected OneToManyField<Supplier, Branch> mBranches;
	
	public static final Supplier get(Context context, int id) {
		return get(context, Supplier.class, id);
	}
	
	public Supplier(Context context) {
		super(context);
		
		mName = new CharField(50);
		mProducts = new ManyToManyField<Supplier, Product>(Supplier.class, Product.class);
		mBranches = new OneToManyField<Supplier, Branch>(Supplier.class, Branch.class);
	}
	
	public void setName(String name) {
		mName.set(name);
	}
	
	public List<Product> getProducts() {
		return mProducts.get(mContext, this);
	}
	
	public void addBranch(Branch b) {
		mBranches.add(this, b);
	}
	
	public List<Branch> getBranches() {
		return mBranches.get(mContext, this);
	}
}
