package com.eduardocarlos.productivityApp.services.exceptions;

public class ObjectNotFoundException extends RuntimeException{

    public ObjectNotFoundException(Class<?> objectType){
        super("Object of type: " + objectType.getName() + " not found");
    }

    public ObjectNotFoundException(String message){
        super("ObjectNotFound: " + message);
    }

}
