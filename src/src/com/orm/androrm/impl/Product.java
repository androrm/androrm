package com.orm.androrm.impl;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.orm.androrm.CharField;
import com.orm.androrm.ForeignKeyField;
import com.orm.androrm.IntegerField;
import com.orm.androrm.Model;
import com.orm.androrm.NoSuchFieldException;

public class Product extends Model {

	public static final Product get(Context context, int id) {
		return get(context, Product.class, id);
	}
	
	public static final List<Product> filter(Context context, 
			Map<String, 
			Object> filter) 
	throws NoSuchFieldException {
		
		return filter(context, Product.class, filter);
	}
	
	protected CharField mName;
	protected IntegerField mPrice;
	protected ForeignKeyField<Supplier> mSupplier;
	
	public Product(Context context) {
		super(context);
		
		mName = new CharField(50);
		mPrice = new IntegerField(2);
		mSupplier = new ForeignKeyField<Supplier>(Supplier.class);
	}

	public void setName(String name) {
		mName.set(name);
	}
	
	public String getName() {
		return mName.get(mContext);
	}
	
	public void setSupplier(Supplier s) {
		mSupplier.set(s);
	}
	
	public Supplier getSupplier() {
		return mSupplier.get(mContext);
	}
}
