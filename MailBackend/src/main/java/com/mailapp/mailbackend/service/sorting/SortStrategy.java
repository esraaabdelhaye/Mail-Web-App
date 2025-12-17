package com.mailapp.mailbackend.service.sorting;

import org.springframework.data.domain.Sort;

public interface SortStrategy {
    Sort getSort();
    String getStrategyName();
}