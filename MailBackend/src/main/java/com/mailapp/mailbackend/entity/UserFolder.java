package com.mailapp.mailbackend.entity;

import jakarta.persistence.*;

@Table(name = "user_folders")

@Entity
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

    public UserFolder() {
    }

    public UserFolder(Long folderId, String folderName, User user) {
        this.folderId = folderId;
        this.folderName = folderName;
        this.folderOwner = user;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public User getFolderOwner() {
        return folderOwner;
    }

    public void setFolderOwner(User user) {
        this.folderOwner = user;
    }
}
