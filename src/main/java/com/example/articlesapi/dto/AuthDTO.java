package com.example.articlesapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthDTO {
    private int id;
    private String username;
    private String token;
}
