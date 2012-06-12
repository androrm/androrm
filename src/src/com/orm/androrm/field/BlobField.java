package com.orm.androrm.field;

import android.content.ContentValues;
import android.database.Cursor;

public class BlobField extends DataField<byte[]> {

	public BlobField() {
		mType = "blob";
	}
	
	@Override
	public void putData(String fieldName, ContentValues values) {
		values.put(fieldName, get());
	}

	@Override
	public void set(Cursor c, String fieldName) {
		mValue = c.getBlob(c.getColumnIndexOrThrow(fieldName));
	}

	@Override
	public void reset() {
		mValue = null;
	}

}
