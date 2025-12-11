package com.mailapp.mailbackend.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "contact_emails")
@Data // For easy access to setters and getters
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
