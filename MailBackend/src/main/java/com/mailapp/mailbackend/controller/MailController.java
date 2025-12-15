package com.mailapp.mailbackend.controller;


import com.mailapp.mailbackend.dto.EmailRequest;
import com.mailapp.mailbackend.dto.MailDetailsDTO;
import com.mailapp.mailbackend.dto.MailPageDTO;
import com.mailapp.mailbackend.service.Mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

@RestController
@RequestMapping("/email")
@CrossOrigin(origins = "http://localhost:4200")
public class MailController {

    @Autowired
    private MailService mailService;


    @GetMapping("/page")
    public ResponseEntity<MailPageDTO> getEmails(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "Inbox") String folderName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DATE_DESC") String sortBy)
    {
        MailPageDTO pageDTO = mailService.getPaginatedMail(userId, folderName, page, size, sortBy);
        return ResponseEntity.ok(pageDTO);
    }

    @GetMapping("/getDetails")
    public ResponseEntity<MailDetailsDTO> getMailDetails(@RequestParam Long userId, @RequestParam Long mailId){
        MailDetailsDTO dto = mailService.getMailDetails(userId, mailId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping(value = "/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> sendEmail(@RequestPart("email") EmailRequest emailRequest,
                                            @RequestPart(value = "files", required = false) List<MultipartFile> files) throws Exception {

        mailService.sendEmail(emailRequest, files);
        return ResponseEntity.ok(("Email processed successfully"));
    }

    @PutMapping("/move")
    public ResponseEntity<String> moveEmails(
            @RequestParam Long userId,
            @RequestParam List<Long> mailId,
            @RequestParam String targetFolder) {

        mailService.moveEmailsToFolder(userId, mailId, targetFolder);
        return ResponseEntity.ok("Emails moved successfully");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> permanentlyDeleteEmails(
            @RequestParam Long userId,
            @RequestParam List<Long> mailId) {

        mailService.permanentlyDeleteEmails(userId, mailId);
        return ResponseEntity.ok("Emails permanently deleted");
    }
}
