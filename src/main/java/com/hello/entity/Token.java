package com.hello.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Token {
    private int id;
    private String token;
    private User user;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}

