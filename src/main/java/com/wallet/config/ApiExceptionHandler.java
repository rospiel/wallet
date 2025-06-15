package com.wallet.config;

import com.wallet.DTO.ErrorDto;
import com.wallet.DTO.FieldErrorDto;
import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException error) {


        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorDto.builder().details(error.getConstraintViolations().iterator().next().getMessageTemplate()).build());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorDto.builder().fields(getFieldsErrors(ex.getBindingResult())).build());
    }

    private List<FieldErrorDto> getFieldsErrors(BindingResult bindingResult) {
        if (Objects.isNull(bindingResult)) {
            return List.of();
        }

        return bindingResult.getAllErrors()
                .stream()
                .map(objectError -> {
                    return FieldErrorDto.builder()
                            .name(getPropertyNameFromError(objectError))
                            .userMessage(objectError.getDefaultMessage())
                            .build();
                }).collect(Collectors.toList());
    }

    private String getPropertyNameFromError(ObjectError objectError) {
        if (Objects.isNull(objectError)) {
            return StringUtils.EMPTY;
        }

        return objectError instanceof FieldError ?
                ((FieldError) objectError).getField() : objectError.getObjectName();
    }
}
