package com.mailapp.mailbackend.service.Attachment;


import com.mailapp.mailbackend.entity.Attachment;
import com.mailapp.mailbackend.entity.Mail;
import com.mailapp.mailbackend.repository.AttachmentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class AttachmentService {
    @Autowired
    private AttachmentRepo attachmentRepo;

    public void saveAttachment(Mail mail, MultipartFile file) throws IOException {

        // Use relative folder without leading slash to avoid root path issues on Windows
        Path uploadDir = Paths.get("uploads");

        // Create folder if missing
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Generate unique file name
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // Build full path
        Path path = uploadDir.resolve(filename);

        // Save file to disk (overwrite if needed)
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        // Save metadata to DB
        Attachment att = new Attachment();
        att.setMail(mail);
        att.setFileName(file.getOriginalFilename());
        att.setStoragePath(path.toString());
        att.setFileType(file.getContentType());
        att.setFileSize(file.getSize());

        attachmentRepo.save(att);

    }

}
