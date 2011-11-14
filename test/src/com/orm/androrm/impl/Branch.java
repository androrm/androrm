package com.orm.androrm.impl;

import android.content.Context;

import com.orm.androrm.CharField;
import com.orm.androrm.ForeignKeyField;
import com.orm.androrm.ManyToManyField;
import com.orm.androrm.Model;
import com.orm.androrm.QuerySet;

public class Branch extends Model {

	public static QuerySet<Branch> objects(Context context) {
		return objects(context, Branch.class);
	}
	
	protected CharField mName;
	protected ForeignKeyField<Brand> mBrand;
	protected ManyToManyField<Branch, Supplier> mSuppliers;
	protected ManyToManyField<Branch, Product> mProducts;
	
	public Branch() {
		super();

		mName = new CharField(50);
		mProducts = new ManyToManyField<Branch, Product>(Branch.class, Product.class);
		mSuppliers = new ManyToManyField<Branch, Supplier>(Branch.class, Supplier.class);
		mBrand = new ForeignKeyField<Brand>(Brand.class);
	}
	
	public void setName(String name) {
		mName.set(name);
	}
	
	public String getName() {
		return mName.get();
	}
	
	public void setBrand(Brand brand) {
		mBrand.set(brand);
	}
	
	public Brand getBrand(Context context) {
		return mBrand.get(context);
	}
	
	public void addProduct(Product product) {
		mProducts.add(product);
	}
	
	public QuerySet<Product> getProducts(Context context) {
		return mProducts.get(context, this);
	}
}
