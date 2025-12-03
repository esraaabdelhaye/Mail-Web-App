package com.mailapp.mailbackend.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "contact_emails")
public class ContactEmail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_email_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private Contact contact;

    @Column(name = "email_address")
    private String emailAddress;
}
