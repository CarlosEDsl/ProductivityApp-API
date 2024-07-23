package com.eduardocarlos.productivityApp.services.exceptions;

public class UnauthenticatedUserException extends RuntimeException{

    public UnauthenticatedUserException(){
        super("User isn't authenticated");
    }

}
