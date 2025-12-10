package com.mailapp.mailbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @OneToMany(mappedBy = "sender" ,orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<Mail> sentMails = new HashSet<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true,cascade = CascadeType.ALL)
    private Set<Folder> userFolders = new HashSet<>();

    @OneToMany(mappedBy = "user",orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<Contact> contacts = new HashSet<>();

}