package com.mailapp.mailbackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecipientComposeDTO {
    private Long id;
    private String email;
    private String type;

}
