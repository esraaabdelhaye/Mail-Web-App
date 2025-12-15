package com.mailapp.mailbackend.service.sorting;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component("sortByDateDesc")
public class SortByDateDescStrategy implements SortStrategy {

    @Override
    public Sort getSort() {
        return Sort.by(Sort.Direction.DESC, "sentAt");
    }

    @Override
    public String getStrategyName() {
        return "DATE_DESC";
    }
}