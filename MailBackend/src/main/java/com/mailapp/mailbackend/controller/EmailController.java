package com.mailapp.mailbackend.controller;


import com.mailapp.mailbackend.dto.MailDetailsDTO;
import com.mailapp.mailbackend.service.Mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/email")
@CrossOrigin(origins = "http://localhost:4200")
public class EmailController {
    @Autowired
    private MailService mailService;

    // Get a list of all the emails
    @GetMapping("/all")
    public List<MailDetailsDTO> getEmails(@RequestParam(defaultValue = "inbox") String folderId) {
        return mailService.getEmails(folderId);
    }



}
