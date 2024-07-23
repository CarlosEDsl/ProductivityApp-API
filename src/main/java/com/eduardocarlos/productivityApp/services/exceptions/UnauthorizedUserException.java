package com.eduardocarlos.productivityApp.services.exceptions;

public class UnauthorizedUserException extends RuntimeException{

    public UnauthorizedUserException(){
        super("Authenticated user don't have permission");
    }

    public UnauthorizedUserException(String message){
        super("User don't have permission enough: " + message);
    }

}
