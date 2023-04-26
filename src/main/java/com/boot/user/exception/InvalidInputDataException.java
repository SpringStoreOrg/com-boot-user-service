package com.boot.user.exception;

public class InvalidInputDataException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4624485104006561870L;

	public InvalidInputDataException(String message){
        super(message);
    }
}
