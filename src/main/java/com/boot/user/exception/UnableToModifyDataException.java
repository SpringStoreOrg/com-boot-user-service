package com.boot.user.exception;

public class UnableToModifyDataException  extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8367698541607582380L;

	public UnableToModifyDataException(String message){
        super(message);
    }
}
