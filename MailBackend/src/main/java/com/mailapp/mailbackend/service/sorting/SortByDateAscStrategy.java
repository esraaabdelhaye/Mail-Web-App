package com.mailapp.mailbackend.service.sorting;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component("sortByDateAsc")
public class SortByDateAscStrategy implements SortStrategy {

    @Override
    public Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "sentAt");
    }

    @Override
    public String getStrategyName() {
        return "DATE_ASC";
    }
}