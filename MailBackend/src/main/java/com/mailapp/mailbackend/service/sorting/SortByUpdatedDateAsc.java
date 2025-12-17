package com.mailapp.mailbackend.service.sorting;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component("sortByUpdatedDateAsc")
public class SortByUpdatedDateAsc implements SortStrategy {

    @Override
    public Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "mail.updatedAt");
    }

    @Override
    public String getStrategyName() {
        return "UPDATED_DATE_ASC";
    }
}