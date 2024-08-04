package com.eduardocarlos.productivityApp.exceptions;

import com.eduardocarlos.productivityApp.services.exceptions.BeforeNowModificationException;
import com.eduardocarlos.productivityApp.services.exceptions.ObjectNotFoundException;
import com.eduardocarlos.productivityApp.services.exceptions.UnauthenticatedUserException;
import com.eduardocarlos.productivityApp.services.exceptions.UnauthorizedUserException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;

@Slf4j(topic = "GLOBAL_EXCEPTION_HANDLER")
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler implements AuthenticationFailureHandler {

    @Value("${server.error.include-exception}")
    private boolean printstackTrace;

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    private ResponseEntity<ErrorResponse> SQLIntegrityConstraitViolationHandler(SQLIntegrityConstraintViolationException exception){
        ErrorResponse error = new ErrorResponse(HttpStatus.CONFLICT.value(), exception.getMessage());
        if(printstackTrace){
            error.setStackTree(Arrays.toString(exception.getStackTrace()));
        }
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(UnauthenticatedUserException.class)
    private ResponseEntity<ErrorResponse> unauthenticatedUserHandler(UnauthenticatedUserException unauthenticatedUserException){
        ErrorResponse error = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), unauthenticatedUserException.getMessage());
        if(printstackTrace){
            error.setStackTree(Arrays.toString(unauthenticatedUserException.getStackTrace()));
        }
        return ResponseEntity.status(error.getStatus()).body(error);

    }

    @ExceptionHandler(ObjectNotFoundException.class)
    private ResponseEntity<ErrorResponse> objectNotFoundHandler(ObjectNotFoundException exception){
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), exception.getMessage());
        if(printstackTrace){
            error.setStackTree(Arrays.toString(exception.getStackTrace()));
        }
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(UnauthorizedUserException.class)
    private ResponseEntity<ErrorResponse> unauthorizedUserHandler(UnauthorizedUserException exception) {
        ErrorResponse error =  new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
        if(printstackTrace){
            error.setStackTree(Arrays.toString(exception.getStackTrace()));
        }
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(BeforeNowModificationException.class)
    private ResponseEntity<ErrorResponse> beforeNowModificationHandler(BeforeNowModificationException exception) {
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
        if(printstackTrace){
            error.setStackTree(Arrays.toString(exception.getStackTrace()));
        }
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        int status = HttpStatus.UNAUTHORIZED.value();
        response.setStatus(status);
        response.setContentType("application/json");
        ErrorResponse error = new ErrorResponse(status, "Email and password dont match");
        response.getWriter().append(error.toJson());
    }
}
