/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qd.controllers;

import com.qd.dto.AuthResponse;
import com.qd.dto.LoginRequest;
import com.qd.dto.RegisterRequest;
import com.qd.service.UserService;
import com.qd.utils.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.bind.annotation.*;
/**
 *
 * @author User
 */

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins="*")
public class AuthApiController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtProvider jwtProvider;
    
    @PostMapping(value="/register",consumes={"multipart/form-data"})
    public ResponseEntity<AuthResponse> register(@ModelAttribute RegisterRequest registerRequest){
        AuthResponse response=userService.register(registerRequest);
        
        if(response.isSuccess()) 
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        
    }
    
    @PostMapping(value="/login")
    public ResponseEntity<AuthResponse> register(@RequestBody LoginRequest loginRequest)//@RequestBody la json thô
    {
        
        AuthResponse response=userService.login(loginRequest.getUsername(), loginRequest.getPassword());

        if(response.isSuccess()) {
            
            String token= jwtProvider.generateToken(loginRequest.getUsername());
            return ResponseEntity.ok(response); //Tra ma 200 + Token JWT
        }
            
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); //Ma 401
        
    }
}