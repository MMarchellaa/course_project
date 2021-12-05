package com.markmihalkovich.course.validations;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.Map;

@Service
public class ResponseErrorValidation {

    public ResponseEntity<Object> mapValidationService(BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();

            result.getAllErrors().forEach(error -> errorMap.put(error.getCode(), error.getDefaultMessage()));

            result.getFieldErrors().forEach(error -> errorMap.put(error.getField(), error.getDefaultMessage()));

            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
        }
        return null;
    }
}
