package com.example.articlesapi.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import com.example.articlesapi.configuration.CustomAuthenticationProvider;
import com.example.articlesapi.dto.AuthDTO;
import com.example.articlesapi.dto.UserDTO;
import com.example.articlesapi.jwt.JwtService;
import com.example.articlesapi.models.User;
import com.example.articlesapi.services.UserService;
import com.example.articlesapi.util.IncorrectJSONException;
import com.example.articlesapi.util.UserAlreadyExistsException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;
    private final CustomAuthenticationProvider authenticationProvider;
    private final ModelMapper modelMapper;

    @Autowired
    public AuthController(UserService userService, JwtService jwtService, CustomAuthenticationProvider authenticationProvider, ModelMapper modelMapper) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationProvider = authenticationProvider;
        this.modelMapper = modelMapper;
    }

    @PostMapping( "/signup")
    public HttpStatus register(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) throws UserAlreadyExistsException, IncorrectJSONException {
        if (bindingResult.hasErrors()){
            List<FieldError> errors = bindingResult.getFieldErrors();
            StringBuilder str = new StringBuilder();
            for (FieldError error : errors){
                str.append(error.getField()).append(": ").append(error.getDefaultMessage()).append(";\n");
            }
            throw new IncorrectJSONException(str.toString());
        }
        Optional <User> userOptional = userService.getUser(userDTO.getUsername());
        if (userOptional.isPresent()){
            throw new UserAlreadyExistsException("a user with that username already exists");
        }
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        userService.save(user);
        return HttpStatus.ACCEPTED;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDTO> login(@RequestBody @Valid UserDTO userDTO) throws BadCredentialsException {
        Optional<User> userOptional = userService.getUser(userDTO.getUsername());
        if (userOptional.isEmpty()){
            throw new UsernameNotFoundException("there is no user with that username");
        }
        authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword()));
        AuthDTO authDTO = modelMapper.map(userOptional.get(), AuthDTO.class);
        authDTO.setToken(jwtService.generateToken(authDTO.getUsername()));
        return new ResponseEntity<>(authDTO, HttpStatus.OK);

    }
}
