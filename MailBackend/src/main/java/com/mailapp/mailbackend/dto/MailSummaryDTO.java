package com.mailapp.mailbackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

public class MailSummaryDTO {
    public Long id;

    // Nested object for { name, email }
    public MailDetailsDTO.SenderDTO sender;

    public List<String> to;
    public String subject;

    public String preview;

    // Formats Date to ISO String for the frontend
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    public Date sentAt;

    public boolean isRead;
    public int priority; // 1, 2, 3, 4
    public boolean hasAttachments;

}
