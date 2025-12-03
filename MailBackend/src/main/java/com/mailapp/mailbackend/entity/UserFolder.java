package com.mailapp.mailbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "user_folders")
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class UserFolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "folder_id")
    private Long folderId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User folderOwner;

    @Column(name = "folder_name", nullable = false)
    private String folderName;

}
