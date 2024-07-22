package com.eduardocarlos.productivityApp.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private int status;
    private String message;
    private String stackTree;
    private List<ValidationError> errors;

    public ErrorResponse(Integer status, String message){
        this.status = status;
        this.message = message;
    }

    @RequiredArgsConstructor
    @Getter
    @Setter
    private static class ValidationError {

        private final String field;
        private final String message;

    }

    public void addValidationError(String field, String message) {
        if(Objects.isNull(this.errors)){
            this.errors = new ArrayList<>();
        }

        this.errors.add(new ValidationError(field, message));
    }

    public String toJson() {
        return "{\"status\": " + getStatus() + ", " +
                "\"message\": \"" + getMessage() + "\"}";
    }



}
