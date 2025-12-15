package com.mailapp.mailbackend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmailRequest {
    private Long draftId;  // ID of the draft mail (if sending from draft)
    private Long senderId;
    private List<String> to;
    private List<String> cc;
    private List<String> bcc;
    private String subject;
    private String body;
    private int priority;
}

