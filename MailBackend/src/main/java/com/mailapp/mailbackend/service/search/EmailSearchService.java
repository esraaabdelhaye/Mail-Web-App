package com.mailapp.mailbackend.service.search;

import com.mailapp.mailbackend.dto.*;
import com.mailapp.mailbackend.entity.Folder;
import com.mailapp.mailbackend.entity.User;
import com.mailapp.mailbackend.entity.UserMail;
import com.mailapp.mailbackend.repository.EmailSearchRepository;
import com.mailapp.mailbackend.service.Mail.MailService;
import com.mailapp.mailbackend.service.search.handlers.SearchHandler;
import com.mailapp.mailbackend.service.sorting.SortStrategy;
import com.mailapp.mailbackend.service.sorting.SortStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Autowired
    private SortStrategyFactory sortStrategyFactory;


    public MailPageDTO quickSearch(
            Long userId,
            String folderName,
            String query,
            int page,
            int size,
            String sortBy

    ) {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setQuery(query);
        criteria.setFolder(folderName);

        return search(userId, criteria, page, size, sortBy);
    }


    public MailPageDTO advancedSearch(
            Long userId,
            int page,
            int size,
            String sortBy,
            SearchCriteria criteria

    ) {
        return search(userId, criteria, page, size, sortBy);
    }


    private MailPageDTO search(
                Long userId,
                SearchCriteria criteria,
        int page, int size, String sortBy
    ) {
            System.out.println("start search");
        System.out.println(criteria);
            SortStrategy sortStrategy = sortStrategyFactory.getStrategy(sortBy, criteria.getFolder());
            Sort sort = sortStrategy.getSort();

            Pageable pageable = PageRequest.of(page, size, sort);

            // Execute search using repository
            Page<UserMail> mailPage = emailSearchRepository.search(
                    userId,
                    criteria,
                    pageable,
                    searchHandlerChain
            );



            System.out.println("end of search");

            return mailService.getPageDTO(mailPage);
    }
}






