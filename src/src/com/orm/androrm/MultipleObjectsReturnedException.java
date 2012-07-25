package com.orm.androrm;

public class MultipleObjectsReturnedException extends RuntimeException {

	private static final long serialVersionUID = -4457337068353196719L;

	public MultipleObjectsReturnedException(String msg) {
		super(msg);
	}

}
