package com.mailapp.mailbackend.dto;

import com.mailapp.mailbackend.enums.ReceiverType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecipientDTO {
    private Long draftId;
    private String type;
    private String recipientEmail;
}
