package com.example.taskapp.backend.exception;

import com.example.taskapp.backend.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> notFound(
            NotFoundException ex,
            HttpServletRequest req) {

        return build(ex.getMessage(),
                HttpStatus.NOT_FOUND, req);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> badRequest(
            BadRequestException ex,
            HttpServletRequest req) {

        return build(ex.getMessage(),
                HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> forbidden(
            ForbiddenException ex,
            HttpServletRequest req) {

        return build(ex.getMessage(),
                HttpStatus.FORBIDDEN, req);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> conflict(
            ConflictException ex,
            HttpServletRequest req) {

        return build(ex.getMessage(),
                HttpStatus.CONFLICT, req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> fallback(
            Exception ex,
            HttpServletRequest req) {

        return build("Internal server error",
                HttpStatus.INTERNAL_SERVER_ERROR, req);
    }

    private ResponseEntity<ErrorResponse> build(
            String msg,
            HttpStatus status,
            HttpServletRequest req) {

        ErrorResponse err = new ErrorResponse(
                msg,
                status.value(),
                req.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }
}
