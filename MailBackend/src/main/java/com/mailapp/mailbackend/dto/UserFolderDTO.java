package com.mailapp.mailbackend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserFolderDTO {
    public Long folderID;
    public String folderName;
    public Boolean isCustom;
}
