package com.mailapp.mailbackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import java.util.List;

public class EmailDTO {
    public Long id;

    // Nested object for { name, email }
    public SenderDTO sender;

    public List<String> to;
    public String subject;
    public String body;

    // Formats Date to ISO String for the frontend
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    public Date sentAt;

    public boolean isRead;
    public int priority; // 1, 2, 3, 4

    public String folder; // 'inbox', 'trash', etc.

    public List<AttachmentDTO> attachments;



    public static class SenderDTO {
        public String name;
        public String email;

        public SenderDTO() {} // Default constructor for Jackson
        public SenderDTO(String name, String email) {
            this.name = name;
            this.email = email;
        }
    }
}