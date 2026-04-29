package com.hello.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class User {
    private int id;
    private String userName;
    private String password;
    private LocalDateTime createdAt;
}

