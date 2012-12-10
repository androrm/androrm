package com.orm.androrm;

import android.database.sqlite.SQLiteTransactionListener;

public class TransactionListener implements SQLiteTransactionListener {

	public static final TransactionListener getFor(DatabaseAdapter adapter) {
		TransactionListener listener = new TransactionListener();
		listener.mDatabaseAdapter = adapter;
		
		return listener;
	}
	
	private DatabaseAdapter mDatabaseAdapter;
	
	@Override
	public void onBegin() {
		mDatabaseAdapter.increaseTransactionCounter();
	}

	@Override
	public void onCommit() {
		mDatabaseAdapter.decreaseTransactionCounter();
	}

	@Override
	public void onRollback() {
		mDatabaseAdapter.resetTransactionCounter();
	}

}
