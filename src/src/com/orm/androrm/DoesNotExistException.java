package com.orm.androrm;

public class DoesNotExistException extends RuntimeException {
	
	private static final long serialVersionUID = -2499666846908675245L;

	public DoesNotExistException(String msg) {
		super(msg);
	}
}
