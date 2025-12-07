package com.mailapp.mailbackend.service.User.auth;

import com.mailapp.mailbackend.dto.UserDTO;
import com.mailapp.mailbackend.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CorrectPassword extends ParentHandler{

    @Autowired
    private UserRepo userRepo;

    public boolean handle(UserDTO userDTO){
        if(userRepo.findByEmail(userDTO.email).getPassword().equals(userDTO.password)){
            return super.handle(userDTO);
        }
        else{
            userDTO.reqState = false;
            userDTO.reqMessage = "incorrect password";
            return false;
        }
    }

}
