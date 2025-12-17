package com.mailapp.mailbackend.service.sorting;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component("sortByPriority")
public class SortByPriorityStrategy implements SortStrategy {

    @Override
    public Sort getSort() {
        // Sort by importance (priority) descending, then by date descending
        return Sort.by(Sort.Direction.DESC, "mail.priority")
                .and(Sort.by(Sort.Direction.DESC, "sentAt"));
    }

    @Override
    public String getStrategyName() {
        return "PRIORITY";
    }
}