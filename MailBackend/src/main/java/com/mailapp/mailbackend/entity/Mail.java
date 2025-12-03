package com.mailapp.mailbackend.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "mails")
public class Mail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mail_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id",  nullable = false)
    private User sender;

    @Column(name = "subject")
    private String subject;


    @Lob
    @Column(name = "body")
    private String body;

    @Column(name = "sent_at")
    private Date sentAt;

    @Column(name = "is_draft")
    private Boolean isDraft;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "deleted_at")
    private Date deletedAt;

}
