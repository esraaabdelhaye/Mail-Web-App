package com.mailapp.mailbackend.service.Mail;

import com.mailapp.mailbackend.dto.EmailDTO;
import com.mailapp.mailbackend.dto.MainMapper;
import com.mailapp.mailbackend.entity.Mail;
import com.mailapp.mailbackend.repository.MailRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MailService {
    @Autowired
    private MailRepo mailRepository;

    @Autowired
    private MainMapper mapper;

    public List<EmailDTO> getEmails(String folderId) {
        // Fetch from DB
        List<Mail> mails = mailRepository.findAll(); // Or filter by folder logic

        // 2. Convert to DTOs using MapStruct
        return mapper.toEmailDTOs(mails);
    }
}
