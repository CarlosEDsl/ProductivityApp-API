package com.eduardocarlos.productivityApp.services.exceptions;

public class BeforeNowModificationException extends RuntimeException{

    public BeforeNowModificationException(Class<?> objectType) {
        super("Can't create a" + objectType.getName() + "before the date 'now'");
    }

}
