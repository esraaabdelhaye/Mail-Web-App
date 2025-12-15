package com.mailapp.mailbackend.service.Mail;

import com.mailapp.mailbackend.dto.*;
import com.mailapp.mailbackend.entity.*;
import com.mailapp.mailbackend.repository.*;
import com.mailapp.mailbackend.service.sorting.SortStrategy;
import com.mailapp.mailbackend.service.sorting.SortStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Sort;

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

    @Autowired
    private SortStrategyFactory sortStrategyFactory;


    public List<MailDetailsDTO> getEmails(String folderId) {
        // Fetch from DB
        List<Mail> mails = mailRepository.findAll(); // Or filter by folder logic

        // 2. Convert to DTOs using MapStruct
        return mainMapper.toEmailDTOs(mails);
    }

    public MailPageDTO getPaginatedMail(Long userId, String folderName,
                                        int page, int size, String sortBy) {
        User user = userRepo.getReferenceById(userId);
        Folder folder = folderRepo.findByUserAndFolderName(user, folderName);

        SortStrategy sortStrategy = sortStrategyFactory.getStrategy(sortBy, folderName);
        Sort sort = sortStrategy.getSort();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<UserMail> mailPage = userMailRepo.findByUserAndFolder(user, folder, pageable);

        return getPageDTO(mailPage);
    }

    public MailPageDTO getPageDTO(Page<UserMail> mailPage){
        List<MailSummaryDTO> mailSummaryDTOS = mailPage.getContent().stream()
                .map(userMail -> mainMapper.toMailSummaryDTO(userMail))
                .collect(Collectors.toList());

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

    /**
     * Retrieves the full details of a specific email for the user.
     * @param userId The ID of the authenticated user.
     * @param mailId The ID of the UserMail entry (not the Mail ID).
     * @return MailDetailsDTO containing all content and attachments.
     */
    public MailDetailsDTO getMailDetails(Long userId, Long mailId) {
        // 1. Find the UserMail entry based on the row ID (mailId from the controller)
        UserMail userMail = userMailRepo.findById(mailId)
                .orElseThrow(() -> new RuntimeException("Email not found for ID: " + mailId));

        // 2. Optional: Security check to ensure the mail belongs to the requesting user
        if (!userMail.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Email does not belong to user.");
        }

        // 3. Mark the email as read (synchronous update)
        if (userMail.getIsRead() == null || !userMail.getIsRead()) {
            userMail.setIsRead(true);
            userMailRepo.save(userMail);
        }

        // 4. Map the full Mail entity to the MailDetailsDTO
        // MapStruct should be configured to handle the nested mappings (Mail -> DTO, Attachment -> DTO).
        MailDetailsDTO dto = mainMapper.toDetailedEmailDTO(userMail);

        return dto;
    }


    public void sendEmail(EmailRequest emailRequest, List<MultipartFile> files) {
        Mail mail;
        
        // If draftId exists, use the existing draft mail (with attachments)
        if (emailRequest.getDraftId() != null) {
            mail = mailRepository.findById(emailRequest.getDraftId())
                    .orElseThrow(() -> new RuntimeException("Draft not found"));
            // Update the draft with final details
            mail.setIsDraft(false);
            mail.setSentAt(new Date());
        } else {
            // Create new mail if no draft
            mail = buildMail(emailRequest);
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

    public void moveEmailsToFolder(Long userId, List<Long> userMailIds, String targetFolderName){
        for (Long userMailId : userMailIds){
            moveEmailToFolder(userId, userMailId, targetFolderName);
        }
    }

    private void moveEmailToFolder(Long userId, Long userMailId, String targetFolderName){
        // Get the email from the db
        UserMail userMail = userMailRepo.findById(userMailId)
                .orElseThrow(() -> new RuntimeException("Email with id "+ userMailId + "was not found in the db"));

        if (!userMail.getUser().getId().equals(userId)){
            throw new RuntimeException("Access denied! An email belonging to user " + userMail.getUser().getId() + "was attempted to be accessed from user " + userId);
        }

        // Get the target folder
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Folder targetFolder = folderRepo.findByUserAndFolderName(user, targetFolderName);

        if (targetFolder == null) {
            throw new RuntimeException("Target folder not found: " + targetFolderName);
        }

        // Update the folder reference
        userMail.setFolder(targetFolder);
        userMail.setMovedAt(new Date());
        userMailRepo.save(userMail);
    }

    public void permanentlyDeleteEmails(Long userId, List<Long> userMailIds) {
        for (Long userMailId : userMailIds) {
            UserMail userMail = userMailRepo.findById(userMailId)
                    .orElseThrow(() -> new RuntimeException("Email not found with ID: " + userMailId));

            // Security check
            if (!userMail.getUser().getId().equals(userId)) {
                throw new RuntimeException("Access denied: Email does not belong to user.");
            }

            // Only allow deletion from Trash
            if (userMail.getFolder() != null && !"Trash".equals(userMail.getFolder().getFolderName())) {
                throw new RuntimeException("Can only permanently delete emails from Trash folder");
            }

            userMailRepo.delete(userMail);
        }
    }
}