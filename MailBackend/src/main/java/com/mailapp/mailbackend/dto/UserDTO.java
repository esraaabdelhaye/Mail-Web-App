package com.mailapp.mailbackend.dto;

public class UserDTO {
    public Long id;
    public String email;
    public String password;
    public String fullName;
    public boolean reqState;
    public String reqMessage;

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}