package com.mailapp.mailbackend.dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DraftDTO {
    private Long draftId;
    private String subject;
    private String body;
    private int priority;
}
