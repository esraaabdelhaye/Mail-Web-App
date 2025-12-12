package com.mailapp.mailbackend.service.search;

import com.mailapp.mailbackend.service.search.handlers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SearchChainConfig {

    @Autowired
    private GlobalSearchHandler globalSearchHandler;

    @Autowired
    private FromSearchHandler fromSearchHandler;

    @Autowired
    private SubjectSearchHandler subjectSearchHandler;

    @Autowired
    private BodySearchHandler bodySearchHandler;

    @Autowired
    private FolderSearchHandler folderSearchHandler;

    @Autowired
    private PrioritySearchHandler prioritySearchHandler;

    @Autowired
    private DateRangeSearchHandler dateRangeSearchHandler;

    @Autowired
    private ReadStatusSearchHandler readStatusSearchHandler;

    @Bean
    public SearchHandler searchHandlerChain() {
        // Build chain
        globalSearchHandler.setNext(fromSearchHandler);
        fromSearchHandler.setNext(subjectSearchHandler);
        subjectSearchHandler.setNext(bodySearchHandler);
        bodySearchHandler.setNext(folderSearchHandler);
        folderSearchHandler.setNext(prioritySearchHandler);
        prioritySearchHandler.setNext(dateRangeSearchHandler);
        dateRangeSearchHandler.setNext(readStatusSearchHandler);

        return globalSearchHandler; // Return head of chain
    }
}


// we will always receive a complete criteria object from the EmailSearchService even if we are searching with a string only
// if it's a simple quick search then all the criteria fields will be null and the handler.handle() will return false in all of them