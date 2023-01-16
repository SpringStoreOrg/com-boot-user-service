package com.boot.user.exception;

public class EmailAlreadyUsedException extends RuntimeException{
    
    public EmailAlreadyUsedException(String message){
        super(message);
    }
}
