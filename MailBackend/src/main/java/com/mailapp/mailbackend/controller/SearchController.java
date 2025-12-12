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
            @PageableDefault(size = 20, sort = "sentAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        MailPageDTO results = emailSearchService.quickSearch(userId, q, pageable);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/advanced")
    public ResponseEntity<MailPageDTO> advancedSearch(
            @RequestParam Long userId,
            @RequestBody SearchCriteria criteria,
            @PageableDefault(size = 20, sort = "sentAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        MailPageDTO results = emailSearchService.advancedSearch(userId, criteria, pageable);
        return ResponseEntity.ok(results);
    }
}