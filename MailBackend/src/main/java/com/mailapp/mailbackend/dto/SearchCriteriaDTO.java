package com.mailapp.mailbackend.dto;


import com.mailapp.mailbackend.enums.Priority;

import java.util.Date;

public class SearchCriteriaDTO {
    private String from;
    private String to;
    private String subject;
    private String body;
    private Boolean hasAttachment;
    private String folder;
    private Date sentAt;
    private Priority priority;
    private boolean HasAttachment;
    private String attachmentName;
}