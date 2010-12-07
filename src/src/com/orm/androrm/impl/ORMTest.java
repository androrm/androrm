package com.orm.androrm.impl;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;

import com.orm.andorm.R;
import com.orm.androrm.DatabaseAdapter;
import com.orm.androrm.Model;

public class ORMTest extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        List<Class<? extends Model>> models = new ArrayList<Class<? extends Model>>();
        models.add(Branch.class);
        models.add(Supplier.class);
        models.add(Product.class);
        
        DatabaseAdapter.setModels(models);
        
        DatabaseAdapter adapter = new DatabaseAdapter(this);
        adapter.drop();
        
        Branch b2 = new Branch(this);
        b2.setName("Cashbuild Pretoria");
        b2.save(1);
        
        Supplier s2 = new Supplier(this);
        s2.addBranch(b2);
        s2.setName("Cashbuild");
        s2.save(1);
        
        Supplier s = Supplier.get(this, 1);
        
        List<Branch> branches = s.getBranches();
        if(branches.size() != 0) {
        	
        }
    }
}