package com.example.articlesapi.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 2, max = 15)
    private String password;
}
