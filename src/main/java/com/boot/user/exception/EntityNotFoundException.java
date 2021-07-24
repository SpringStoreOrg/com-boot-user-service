package com.boot.user.exception;

public class EntityNotFoundException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7825297071944927383L;

	public EntityNotFoundException(String message){
        super(message);
    }
}
