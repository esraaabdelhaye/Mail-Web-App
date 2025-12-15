package com.mailapp.mailbackend.service.sorting;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component("sortByUnread")
public class SortByUnreadStrategy implements SortStrategy {

    @Override
    public Sort getSort() {
        // Sort by read status (unread first), then by date descending
        return Sort.by(Sort.Direction.ASC, "isRead")
                .and(Sort.by(Sort.Direction.DESC, "sentAt"));
    }

    @Override
    public String getStrategyName() {
        return "UNREAD_FIRST";
    }
}