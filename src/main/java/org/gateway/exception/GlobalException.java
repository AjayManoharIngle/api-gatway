package org.gateway.exception;

import java.time.LocalDateTime;

import org.gateway.exception.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

@RestControllerAdvice
public class GlobalException {

	@ExceptionHandler({ApiGatewayException.class,ResponseStatusException.class})
    public ResponseEntity<ErrorResponse> handleRuntimeException(Exception ex, ServerWebExchange request) {
		HttpStatus status = getStatusAsPerException(ex);
		ErrorResponse error = new ErrorResponse(ex.getMessage(), status.value(),LocalDateTime.now());
        return new ResponseEntity<>(error, status);
    }

	@ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, ServerWebExchange request) {
        ErrorResponse error = new ErrorResponse("Internal Server Error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(),LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
	
	private HttpStatus getStatusAsPerException(Exception ex) {
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
	    if (ex instanceof ApiGatewayException) {
	        status = HttpStatus.BAD_REQUEST;  
	    }
	    return status;
	}
}
