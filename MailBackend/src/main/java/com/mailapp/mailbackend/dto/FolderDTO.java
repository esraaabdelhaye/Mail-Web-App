package com.mailapp.mailbackend.dto;

public class FolderDTO {
    public String id;
    public String name;

    // isCustom = false if folder was system created
    public boolean isCustom;

    public FolderDTO() {}
    public FolderDTO(String id, String name, boolean isCustom) {
        this.id = id;
        this.name = name;
        this.isCustom = isCustom;
    }
}