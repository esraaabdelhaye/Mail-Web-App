package com.mailapp.mailbackend.service.Mail;

import com.mailapp.mailbackend.enums.ReceiverType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiverEntry {
    private String receiverEmail;
    private ReceiverType receiverType;

    public ReceiverEntry(String receiverEmail, ReceiverType receiverType) {
        this.receiverEmail = receiverEmail;
        this.receiverType = receiverType;
    }
}
