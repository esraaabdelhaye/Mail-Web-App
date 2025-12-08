package com.mailapp.mailbackend.entity;


import com.mailapp.mailbackend.enums.Priority;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_mails")
public class UserMail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_mail_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mail_id")
    private Mail mail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private Folder folder;

    @Column(name = "sent_at")
    private Date sentAt;

    @Column(name = "is_read")
    private Boolean isRead;

    @Column(name = "importance")
    private Priority importance;

    @Column(name = "is_archived")
    private Boolean isArchived;
}
