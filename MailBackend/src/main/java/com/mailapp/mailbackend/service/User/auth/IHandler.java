package com.mailapp.mailbackend.service.User.auth;

import com.mailapp.mailbackend.dto.UserDTO;

public interface IHandler {
    void setNext(IHandler handler);
    boolean handle(UserDTO userDTO);
}
