package com.mailapp.mailbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ContactDTO {
    public String id;
    public String name;
    public List<String> emails;
}
