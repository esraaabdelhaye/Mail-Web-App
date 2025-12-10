package com.mailapp.mailbackend.service.Mail;

import com.mailapp.mailbackend.dto.EmailDTO;
import com.mailapp.mailbackend.dto.EmailRequest;
import com.mailapp.mailbackend.dto.MailPageDTO;
import com.mailapp.mailbackend.dto.MainMapper;
import com.mailapp.mailbackend.entity.*;
import com.mailapp.mailbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MailService {
    @Autowired
    private MailRepo mailRepository;

    @Autowired
    private UserMailRepo userMailRepo;

    @Autowired
    private FolderRepo folderRepo;

    @Autowired
    private MainMapper mainMapper;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AttachmentRepo attachmentRepo;

    public List<EmailDTO> getEmails(String folderId) {
        // Fetch from DB
        List<Mail> mails = mailRepository.findAll(); // Or filter by folder logic

        // 2. Convert to DTOs using MapStruct
        return mainMapper.toEmailDTOs(mails);
    }

    public MailPageDTO getPaginatedMail(Long userId, String folderName, Pageable pageable) {
        User user = userRepo.getById(userId);
        Folder folder = folderRepo.findByUserAndFolderName(user, folderName);
        Page<UserMail> mailPage = userMailRepo.findByUserAndFolder(user, folder, pageable);

        List<EmailDTO> mailDTOs = mailPage.getContent().stream()
                .map(mainMapper::toEmailDTO)
                .collect(Collectors.toList());

        // Construct and return the Pagination DTO
        MailPageDTO pageDTO = new MailPageDTO();
        pageDTO.setContent(mailDTOs);
        pageDTO.setTotalPages(mailPage.getTotalPages());
        pageDTO.setTotalElements(mailPage.getTotalElements());
        pageDTO.setCurrentPage(mailPage.getNumber());
        pageDTO.setIsFirst(mailPage.isFirst());
        pageDTO.setIsLast(mailPage.isLast());
        pageDTO.setPageSize(mailPage.getSize());

        return pageDTO;
    }


    public void sendEmail(EmailRequest emailRequest, List<MultipartFile> files) {
        Mail mail = buildMail(emailRequest);
        if (files != null && !files.isEmpty())
        {
            try {
                saveAttachments(mail, files);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                throw new RuntimeException("Failed to save attachments", e);
            }
        }

//        sendStrategy.send(mail);



    }


    private Mail buildMail(EmailRequest req) {
        User sender = userRepo.findById(req.getSenderId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return Mail.builder()
                .sender(sender)
                .subject(req.getSubject())
                .body(req.getBody())
                .priority(req.getPriority())
                .sentAt(new Date())
                .isDraft(false)
                .isDeleted(false)
                .build();
    }

    private void saveAttachments(Mail mail, List<MultipartFile> files) throws IOException {
        String folder = "/uploads/";

        for(MultipartFile file : files) {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(folder + filename);
            Files.copy(file.getInputStream(), path);

            Attachment att = new Attachment();
            att.setMail(mail);
            att.setFileName(file.getOriginalFilename());
            att.setStoragePath(path.toString());
            att.setFileType(file.getContentType());
            att.setFileSize(file.getSize());
            attachmentRepo.save(att);
        }
    }
}


