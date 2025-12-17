package com.mailapp.mailbackend.dto;

import com.mailapp.mailbackend.enums.Priority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SearchCriteria {
    private String query;              // Global search string
    private String from;
    private String to;
    private String subject;
    private String body;
    private String folder;
    private Boolean hasAttachment;
    private int priority;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isRead;
}