package com.mailapp.mailbackend.service.sorting;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component("sortBySubject")
public class SortBySubjectStrategy implements SortStrategy {

    @Override
    public Sort getSort() {
        // Sort by subject alphabetically, then by date
        return Sort.by(Sort.Direction.ASC, "mail.subject")
                .and(Sort.by(Sort.Direction.DESC, "sentAt"));
    }

    @Override
    public String getStrategyName() {
        return "SUBJECT";
    }
}