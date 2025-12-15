package com.mailapp.mailbackend.service.sorting;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component("sortBySender")
public class SortBySenderStrategy implements SortStrategy {

    @Override
    public Sort getSort() {
        // Sort by sender's email alphabetically, then by date descending
        return Sort.by(Sort.Direction.ASC, "mail.sender.email")
                .and(Sort.by(Sort.Direction.DESC, "sentAt"));
    }

    @Override
    public String getStrategyName() {
        return "SENDER";
    }
}