
package com.boot.user.exception;

public class DuplicateEntryException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1229500833301404993L;

	public DuplicateEntryException(String message) {
		super(message);
	}

}
