package com.mailapp.mailbackend.service.Mail;

import com.mailapp.mailbackend.dto.*;
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
import java.nio.file.StandardCopyOption;
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

    @Autowired
    private SingleReceiverSend singleReceiverSend;

    @Autowired
    private MultiReceiverSend multiReceiverSend;


    public List<MailDetailsDTO> getEmails(String folderId) {
        // Fetch from DB
        List<Mail> mails = mailRepository.findAll(); // Or filter by folder logic

        // 2. Convert to DTOs using MapStruct
        return mainMapper.toEmailDTOs(mails);
    }

    public MailPageDTO getPaginatedMail(Long userId, String folderName, Pageable pageable) {
        User user = userRepo.getReferenceById(userId);
        Folder folder = folderRepo.findByUserAndFolderName(user, folderName);
        Page<UserMail> mailPage = userMailRepo.findByUserAndFolder(user, folder, pageable);

        return getPageDTO(mailPage);
    }

    public MailPageDTO getPageDTO(Page<UserMail> mailPage) {
        List<MailSummaryDTO> mailSummaryDTOS = mailPage.getContent().stream()
                .map(userMail -> mainMapper.toMailSummaryDTO(userMail))
                .collect(Collectors.toList());

        System.out.println("Num of emails: " + mailSummaryDTOS.size());
        // Construct and return the Pagination DTO
        MailPageDTO pageDTO = new MailPageDTO();
        pageDTO.setContent(mailSummaryDTOS);
        pageDTO.setTotalPages(mailPage.getTotalPages());
        pageDTO.setTotalElements(mailPage.getTotalElements());
        pageDTO.setCurrentPage(mailPage.getNumber());
        pageDTO.setIsFirst(mailPage.isFirst());
        pageDTO.setIsLast(mailPage.isLast());
        pageDTO.setPageSize(mailPage.getSize());

        return pageDTO;
    }

    public MailDetailsDTO getMailDetails(Long userId, Long mailId) {
        User user = userRepo.getReferenceById(userId);
        UserMail userMail = userMailRepo.findByUserAndId(user, mailId);
        return mainMapper.toDetailedEmailDTO(userMail);
    }


    public void sendEmail(EmailRequest emailRequest, List<MultipartFile> files) {
        Mail mail = buildMail(emailRequest);
        mailRepository.save(mail);
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

        SendStrategy strategy = selectStrategy(emailRequest);
        strategy.sendMail(mail, emailRequest);



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

        // Use relative folder without leading slash to avoid root path issues on Windows
        Path uploadDir = Paths.get("uploads");

        // Create folder if missing
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        for (MultipartFile file : files) {
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


    private SendStrategy selectStrategy(EmailRequest req) {
        int total = req.getTo().size() +
                req.getCc().size() +
                req.getBcc().size();
        if (total > 1) return multiReceiverSend;
        return singleReceiverSend;
    }

}
