package com.mailapp.mailbackend.controller;

import com.mailapp.mailbackend.dto.MailPageDTO;
import com.mailapp.mailbackend.dto.SearchCriteria;
import com.mailapp.mailbackend.service.search.EmailSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email/search")
@CrossOrigin(origins = "http://localhost:4200")
public class SearchController {

    @Autowired
    private EmailSearchService emailSearchService;

    @GetMapping("/quick")
    public ResponseEntity<MailPageDTO> quickSearch(
            @RequestParam Long userId,
            @RequestParam String q,
            @RequestParam(defaultValue = "Inbox") String folderName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DATE_DESC") String sortBy
    ) {
        MailPageDTO results = emailSearchService.quickSearch(userId, folderName, q, page, size, sortBy);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/advanced")
    public ResponseEntity<MailPageDTO> advancedSearch(
            @RequestParam Long userId,
            @RequestBody SearchCriteria criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DATE_DESC") String sortBy
    ) {
        MailPageDTO results = emailSearchService.advancedSearch(userId,page, size, sortBy, criteria);
        return ResponseEntity.ok(results);
    }
}