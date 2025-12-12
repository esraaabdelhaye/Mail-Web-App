package com.mailapp.mailbackend.service.Mail;

import com.mailapp.mailbackend.dto.EmailDTO;
import com.mailapp.mailbackend.dto.SearchCriteriaDTO;
import com.mailapp.mailbackend.entity.Mail;
import com.mailapp.mailbackend.entity.UserMail;
import com.mailapp.mailbackend.repository.MailRepo;
import com.mailapp.mailbackend.repository.UserMailRepo;
import com.mailapp.mailbackend.service.User.auth.EmailExists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private MailRepo mailRepo;

    @Autowired
    private UserMailRepo userMailRepo;
    @Autowired
    private MailService mailService;


}
