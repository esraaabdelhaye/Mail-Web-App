package com.mailapp.mailbackend.controller;

import com.mailapp.mailbackend.dto.ComposeDraftDTO;
import com.mailapp.mailbackend.dto.CreateDraftDTO;
import com.mailapp.mailbackend.dto.DraftDTO;
import com.mailapp.mailbackend.dto.RecipientDTO;
import com.mailapp.mailbackend.service.Mail.DraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/email/draft")
@CrossOrigin(origins = "http://localhost:4200")
public class DraftController {

    @Autowired
    private DraftService draftService;




    @PostMapping("/create")
    public ResponseEntity<Map<String, Long>> createDraft(@RequestBody CreateDraftDTO request) {
        Long userId = request.getSenderId();
        Long draftId = draftService.createDraft(userId);
        return ResponseEntity.ok(Map.of(
                "draftId", draftId
        ));
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveDraft(@RequestBody DraftDTO request) {
        draftService.saveDraft(request);
        return ResponseEntity.ok("Draft saved successfully");
    }

    @PostMapping("/recipient/add")
    public ResponseEntity<Map<String, Long>> saveRecipient(@RequestBody RecipientDTO request) {
        Long mailReceiverId = draftService.saveRecipient(request);
        return ResponseEntity.ok(Collections.singletonMap("id", mailReceiverId));
    }

    @DeleteMapping("/recipient/{recipientId}")
    public ResponseEntity<Void> deleteDraftRecipient(@PathVariable Long recipientId) {
        draftService.deleteDraftRecipientById(recipientId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("recipient/validate")
    public ResponseEntity<Boolean> validateDraftRecipient(@RequestBody String recipientEmail) {
        return ResponseEntity.ok(draftService.isValid(recipientEmail));
    }

    @GetMapping("/compose/{draftId}")
    public ResponseEntity<ComposeDraftDTO> getComposeDraft(@PathVariable Long draftId) {
        ComposeDraftDTO draft = draftService.getDraftForCompose(draftId);
        return ResponseEntity.ok(draft);
    }

//    @PostMapping(value = "/attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<String> uploadDraftAttachment(
//            @RequestParam("file") MultipartFile file,
//            @RequestParam("draftId") Long draftId) {
//
//        draftService.addAttachmentToDraft(draftId, file);
//        return ResponseEntity.ok("Attachment uploaded");
//    }
//
//    @DeleteMapping("/attachment")
//    public ResponseEntity<String> removeDraftAttachment(
//            @RequestParam String fileName,
//            @RequestParam Long draftId) {
//        draftService.removeAttachmentFromDraft(draftId, fileName);
//        return ResponseEntity.ok("Attachment deleted");
//    }


}
