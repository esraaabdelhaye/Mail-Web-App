package com.mailapp.mailbackend.controller;

import com.mailapp.mailbackend.dto.MailPageDTO;
import com.mailapp.mailbackend.service.Mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@CrossOrigin(origins = "http://localhost:4200")
public class MailController {

    @Autowired
    private MailService mailService;


    @GetMapping("/")
    public ResponseEntity<MailPageDTO> getInbox(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "Inbox") String folderName,
            @PageableDefault(size = 10, sort = "sentAt", direction = Sort.Direction.DESC) Pageable pageable)
    {

        MailPageDTO pageDTO = mailService.getPaginatedMail(userId, folderName, pageable);
        return ResponseEntity.ok(pageDTO);
    }

}
