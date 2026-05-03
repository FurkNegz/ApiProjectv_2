package com.hello.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ErrorLog {

    private String errorMessage;
    private String stackTrace;
    private String endpoint;
    private String username;
    private String exceptionType;
    private LocalDateTime createdAt;
}