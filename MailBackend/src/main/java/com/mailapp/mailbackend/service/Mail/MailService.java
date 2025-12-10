package com.mailapp.mailbackend.service.Mail;

import com.mailapp.mailbackend.dto.EmailDTO;
import com.mailapp.mailbackend.dto.MailPageDTO;
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

}
