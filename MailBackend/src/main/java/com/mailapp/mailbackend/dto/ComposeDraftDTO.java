package com.mailapp.mailbackend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ComposeDraftDTO {
    private Long draftId;
    private List<RecipientComposeDTO> to;
    private List<RecipientComposeDTO> cc;
    private List<RecipientComposeDTO> bcc;
    private String subject;
    private String body;
    private int priority;
    private List<AttachmentDTO> attachments;
}
