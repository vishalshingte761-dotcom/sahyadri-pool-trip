package com.sahyadri.sahyadripooltrip.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import java.nio.file.NoSuchFileException;
import org.springframework.web.bind.MissingServletRequestParameterException;

@RestControllerAdvice
public class GlobalExceptionHandler {
        // =====================================================
        // MISSING REQUEST PARAMETER - 400
        // =====================================================
        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<Map<String, Object>> handleMissingRequestParameter(
                        MissingServletRequestParameterException ex) {

                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Required parameter is missing: " + ex.getParameterName());

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(response);
        }
        // =====================================================
        // VALIDATION ERROR - 400
        // =====================================================
        // =====================================================
        // INVALID JSON REQUEST - 400
        // =====================================================

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<Map<String, Object>> handleInvalidJson(
                        HttpMessageNotReadableException ex) {

                Map<String, Object> response = new HashMap<>();

                response.put("success", false);
                response.put("message", "Invalid request body");

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(response);
        }

        // =====================================================
        // RESOURCE NOT FOUND - 404
        // =====================================================

        @ExceptionHandler(NoResourceFoundException.class)
        public ResponseEntity<Map<String, Object>> handleResourceNotFound(
                        NoResourceFoundException ex) {

                Map<String, Object> response = new HashMap<>();

                response.put("success", false);
                response.put("message", "Resource not found");

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(response);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, Object>> handleValidationException(
                        MethodArgumentNotValidException ex) {

                Map<String, String> errors = new HashMap<>();

                ex.getBindingResult()
                                .getFieldErrors()
                                .forEach(error -> errors.put(
                                                error.getField(),
                                                error.getDefaultMessage()));

                Map<String, Object> response = new HashMap<>();

                response.put("success", false);
                response.put("message", "Validation failed");
                response.put("errors", errors);

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(response);
        }

        // =====================================================
        // BAD REQUEST - 400
        // =====================================================

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<Map<String, Object>> handleIllegalArgument(
                        IllegalArgumentException ex) {

                Map<String, Object> response = new HashMap<>();

                response.put("success", false);
                response.put("message", ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(response);
        }

        // =====================================================
        // ACCESS DENIED - 403
        // =====================================================

        @ExceptionHandler(AuthorizationDeniedException.class)
        public ResponseEntity<Map<String, Object>> handleAuthorizationDenied(
                        AuthorizationDeniedException ex) {

                Map<String, Object> response = new HashMap<>();

                response.put("success", false);
                response.put("message", "Access denied");

                return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(response);
        }

        // =====================================================
        // FILE NOT FOUND / INVALID DOCUMENT PATH - 404
        // =====================================================
        @ExceptionHandler(NoSuchFileException.class)
        public ResponseEntity<Map<String, Object>> handleNoSuchFile(
                        NoSuchFileException ex) {

                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Document not found");

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(response);
        }
        // =====================================================
        // GENERAL ERROR - 500
        // =====================================================

        @ExceptionHandler(Exception.class)
        public ResponseEntity<Map<String, Object>> handleGeneralException(
                        Exception ex) {

                Map<String, Object> response = new HashMap<>();

                response.put("success", false);
                response.put("message", "Something went wrong");

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(response);
        }

        @ExceptionHandler(MissingServletRequestPartException.class)
        public ResponseEntity<Map<String, Object>> handleMissingServletRequestPart(
                        MissingServletRequestPartException ex) {

                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Required file is missing: " + ex.getRequestPartName());

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        @ExceptionHandler(MaxUploadSizeExceededException.class)
        public ResponseEntity<Map<String, Object>> handleMaxUploadSizeExceeded(
                        MaxUploadSizeExceededException ex) {

                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Uploaded file is too large");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
}