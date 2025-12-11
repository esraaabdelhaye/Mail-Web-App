package com.mailapp.mailbackend.controller;

import com.mailapp.mailbackend.dto.EmailDTO;
import com.mailapp.mailbackend.dto.SearchCriteriaDTO;
import com.mailapp.mailbackend.service.Mail.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;


}



// later
// (1) apply pagination on search results
// (2) use strategy pattern --> matching strategies