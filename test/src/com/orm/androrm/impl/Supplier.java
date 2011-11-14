package com.orm.androrm.impl;

import java.util.List;

import android.content.Context;

import com.orm.androrm.CharField;
import com.orm.androrm.ForeignKeyField;
import com.orm.androrm.ManyToManyField;
import com.orm.androrm.Model;
import com.orm.androrm.QuerySet;

public class Supplier extends Model {

	protected CharField mName;
	protected ForeignKeyField<Brand> mBrand;
	protected ManyToManyField<Supplier, Product> mProducts;
	protected ManyToManyField<Supplier, Branch> mBranches;
	
	public static final QuerySet<Supplier> objects(Context context) {
		return objects(context, Supplier.class);
	}
	
	public Supplier() {
		super();
		
		mName = new CharField(50);
		mProducts = new ManyToManyField<Supplier, Product>(Supplier.class, Product.class);
		mBranches = new ManyToManyField<Supplier, Branch>(Supplier.class, Branch.class);
		mBrand = new ForeignKeyField<Brand>(Brand.class);
		mBrand.doNotCascade();
	}
	
	public void setName(String name) {
		mName.set(name);
	}
	
	public QuerySet<Product> getProducts(Context context) {
		return mProducts.get(context, this);
	}
	
	public void addProduct(Product p) {
		mProducts.add(p);
	}
	
	public void addProducts(List<Product> products) {
		mProducts.addAll(products);
	}
	
	public int productCount(Context context) {
		return mProducts.get(context, this).count();
	}
	
	public void addBranch(Branch b) {
		mBranches.add(b);
	}
	
	public void setBrand(Brand brand) {
		mBrand.set(brand);
	}
	
	public Brand getBrand(Context context) {
		return mBrand.get(context);
	}
}
