package com.mailapp.mailbackend.service.sorting;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component("sortByUpdatedDateDesc")
public class SortByUpdatedDateDesc implements SortStrategy {

    @Override
    public Sort getSort() {
        return Sort.by(Sort.Direction.DESC, "mail.updatedAt");
    }

    @Override
    public String getStrategyName() {
        return "UPDATED_DATE_ASC";
    }
}