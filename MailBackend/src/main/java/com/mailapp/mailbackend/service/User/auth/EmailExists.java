package com.mailapp.mailbackend.service.User.auth;

import com.mailapp.mailbackend.dto.UserDTO;
import com.mailapp.mailbackend.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

@Component
public class EmailExists extends ParentHandler{

    @Autowired
    private UserRepo userRepo;

    public boolean handle(UserDTO userDTO){
        if(userRepo.findByEmail(userDTO.email) != null){
            return super.handle(userDTO);
        }
        else{
            userDTO.reqState = false;
            userDTO.reqMessage = "Sorry, Email Not Found";
            return false;
        }
    }
}
