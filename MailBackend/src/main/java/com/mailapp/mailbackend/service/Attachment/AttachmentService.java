package com.mailapp.mailbackend.service.Attachment;

import com.mailapp.mailbackend.entity.Attachment;
import com.mailapp.mailbackend.entity.Mail;
import com.mailapp.mailbackend.repository.AttachmentRepo;
import com.mailapp.mailbackend.repository.MailRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class AttachmentService {

    @Autowired
    private AttachmentRepo attachmentRepo;

    @Autowired
    private MailRepo mailRepo;

    @Autowired
    private FileStorageService fileStorageService;

    public Attachment uploadAttachment(Long mailId, MultipartFile file) throws IOException {
        Mail mail = mailRepo.findById(mailId)
                .orElseThrow(() -> new RuntimeException("Mail not found with ID: " + mailId));

        // Store file
        String fileName = fileStorageService.storeFile(file);

        // Create attachment record
        Attachment attachment = Attachment.builder()
                .mail(mail)
                .fileName(fileName)
                .originalFileName(file.getOriginalFilename())
                .storagePath(fileStorageService.getFilePath(fileName).toString())
                .fileSize(file.getSize())
                .fileType(file.getContentType())
                .build();

        return attachmentRepo.save(attachment);
    }

    public List<Attachment> getAttachmentsByMailId(Long mailId) {
        return attachmentRepo.findByMailId(mailId);
    }

    public Attachment getAttachment(Long attachmentId) {
        return attachmentRepo.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found with ID: " + attachmentId));
    }

    public void deleteAttachment(Long attachmentId) throws IOException {
        Attachment attachment = getAttachment(attachmentId);
        fileStorageService.deleteFile(attachment.getFileName());
        attachmentRepo.delete(attachment);
    }
}