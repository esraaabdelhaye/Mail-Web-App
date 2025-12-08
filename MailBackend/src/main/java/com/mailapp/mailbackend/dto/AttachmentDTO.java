package com.mailapp.mailbackend.dto;

public class AttachmentDTO {
    public String id;
    public String name;
    public String size;
    public String type;

    public AttachmentDTO() {}
    public AttachmentDTO(String id, String name, String size, String type) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.type = type;
    }
}