package com.mailapp.mailbackend.controller;


import com.mailapp.mailbackend.dto.UserDTO;
import com.mailapp.mailbackend.enums.ChainType;
import com.mailapp.mailbackend.service.User.UserService;
import com.mailapp.mailbackend.service.User.auth.ChainFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    @Autowired
    UserService userService;

    // Register
    @PostMapping("/register")
    public UserDTO register(@RequestBody UserDTO userDTO) throws Exception {
        return userService.register(userDTO);
    }

    @PostMapping("/login")
    public UserDTO login(@RequestBody UserDTO userDTO) throws Exception {
        return userService.login(userDTO);
    }

}
