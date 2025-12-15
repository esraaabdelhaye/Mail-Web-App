package com.mailapp.mailbackend.dto;

public class AttachmentDTO {
    public String id;
    public String fileName;
    public String fileSize;
    public String fileType;

    public AttachmentDTO() {}
    public AttachmentDTO(String id, String fileName, String fileSize, String fileType) {
        this.id = id;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
    }
}