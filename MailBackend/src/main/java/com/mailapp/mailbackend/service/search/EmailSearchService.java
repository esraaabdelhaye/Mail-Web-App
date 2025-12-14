package com.mailapp.mailbackend.service.search;

import com.mailapp.mailbackend.dto.*;
import com.mailapp.mailbackend.entity.UserMail;
import com.mailapp.mailbackend.repository.EmailSearchRepository;
import com.mailapp.mailbackend.service.Mail.MailService;
import com.mailapp.mailbackend.service.search.handlers.SearchHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class EmailSearchService {

    @Autowired
    private EmailSearchRepository emailSearchRepository;

    @Autowired
    private SearchHandler searchHandlerChain;

    @Autowired
    private MailService mailService;

    public MailPageDTO quickSearch(
            Long userId,
            String query,
            Pageable pageable
    ) {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setQuery(query);

        return search(userId, criteria, pageable);
    }


    public MailPageDTO advancedSearch(
            Long userId,
            SearchCriteria criteria,
            Pageable pageable
    ) {
        return search(userId, criteria, pageable);
    }


    private MailPageDTO search(
            Long userId,
            SearchCriteria criteria,
            Pageable pageable
    ) {
        // Execute search using repository
        Page<UserMail> mailPage = emailSearchRepository.search(
                userId,
                criteria,
                pageable,
                searchHandlerChain
        );

        System.out.println("from quickSearch: " + mailPage.getContent());

       return mailService.getPageDTO(mailPage);
    }
}