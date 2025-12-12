package com.mailapp.mailbackend.service.Mail;

import com.mailapp.mailbackend.dto.MailDetailsDTO;
import com.mailapp.mailbackend.dto.MailPageDTO;
import com.mailapp.mailbackend.dto.MailSummaryDTO;
import com.mailapp.mailbackend.dto.MainMapper;
import com.mailapp.mailbackend.entity.Folder;
import com.mailapp.mailbackend.entity.Mail;
import com.mailapp.mailbackend.entity.User;
import com.mailapp.mailbackend.entity.UserMail;
import com.mailapp.mailbackend.repository.FolderRepo;
import com.mailapp.mailbackend.repository.MailRepo;
import com.mailapp.mailbackend.repository.UserMailRepo;
import com.mailapp.mailbackend.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
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
}



//
//    public MailDetailsDTO getEmailDTO(Long mailId) {
//        UserMail userMail = userMailRepo.getReferenceById(mailId);
//        Mail mail = userMail.getMail();
//
//        MailDetailsDTO dto = new MailDetailsDTO();
//
//        // Basic mail data
//        dto.id = mail.getId();
//        dto.subject = mail.getSubject();
//        dto.body = mail.getBody();
//
//        // Sender
//        dto.sender = new MailDetailsDTO.SenderDTO(
//                mail.getSender().getFullName(),
//                mail.getSender().getEmail()
//        );
//
//        // Sent at – prefer the one from Mail
//        dto.sentAt = mail.getSentAt();
//
//        // User-specific fields
//        dto.isRead = userMail.getIsRead() != null && userMail.getIsRead();
//        dto.priority = userMail.getImportance() != null
//                ? userMail.getImportance().getValue()    // convert enum → int, see below
//                : 1;
//
//        dto.folder = userMail.getFolder().getFolderName();
//
//        // Attachments (if you have an entity)
//        // dto.attachments = mail.getAttachments()
//        //      .stream()
//        //      .map(a -> new AttachmentDTO(a.getName(), a.getUrl()))
//        //      .toList();
//
//        return dto;
//    }