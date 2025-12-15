package com.mailapp.mailbackend.controller;

import com.mailapp.mailbackend.entity.Attachment;
import com.mailapp.mailbackend.service.Attachment.AttachmentService;
import com.mailapp.mailbackend.service.Attachment.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/attachments")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<Attachment> uploadAttachment(
            @RequestParam("mailId") Long mailId,
            @RequestParam("file") MultipartFile file) {
        try {
            Attachment attachment = attachmentService.uploadAttachment(mailId, file);
            return ResponseEntity.ok(attachment);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/mail/{mailId}")
    public ResponseEntity<List<Attachment>> getAttachmentsByMail(@PathVariable Long mailId) {
        List<Attachment> attachments = attachmentService.getAttachmentsByMailId(mailId);
        return ResponseEntity.ok(attachments);
    }

    @GetMapping("/download/{attachmentId}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long attachmentId) {
        try {
            Attachment attachment = attachmentService.getAttachment(attachmentId);
            Path filePath = fileStorageService.getFilePath(attachment.getFileName());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(attachment.getFileType()))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + attachment.getOriginalFileName() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<String> deleteAttachment(@PathVariable Long attachmentId) {
        try {
            attachmentService.deleteAttachment(attachmentId);
            return ResponseEntity.ok("Attachment deleted successfully");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to delete attachment");
        }
    }
}