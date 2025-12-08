package com.mailapp.mailbackend.service.User.auth;

import com.mailapp.mailbackend.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class LongEnough extends ParentHandler {


    public boolean handle(UserDTO userDTO){
        if(userDTO.password.length()>=8){
            System.out.println("Password length exceeded");

            return super.handle(userDTO);
        }
        else{
            userDTO.reqState = false;
            userDTO.reqMessage = "the password is too short, it must be more than 8 characters";
            return false;
        }
    }
}
