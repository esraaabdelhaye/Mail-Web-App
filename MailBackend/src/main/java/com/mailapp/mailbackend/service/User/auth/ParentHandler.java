package com.mailapp.mailbackend.service.User.auth;

import com.mailapp.mailbackend.dto.UserDTO;


public class ParentHandler implements IHandler {

    IHandler nextHandler = null;

    public ParentHandler(IHandler nextHandler) {
        this.nextHandler = nextHandler;
    }
    public ParentHandler() {
        this(null);
    }


    @Override
    public void setNext(IHandler handler){
        this.nextHandler = handler;
    }

    @Override
    public boolean handle(UserDTO user){


        if(nextHandler == null){
            System.out.println("Parent: nextHandler is null");
            user.reqState = true;
            user.reqMessage = "Successfully authenticated";
            System.out.println("Req Message: " + user.reqMessage);
            return true;
        }

        return nextHandler.handle(user);
    }
}
