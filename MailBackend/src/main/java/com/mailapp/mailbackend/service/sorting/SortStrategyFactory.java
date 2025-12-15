package com.mailapp.mailbackend.service.sorting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SortStrategyFactory {

    private final Map<String, SortStrategy> strategies = new HashMap<>();

    @Autowired
    public SortStrategyFactory(
            @Qualifier("sortByDateDesc") SortStrategy sortByDateDesc,
            @Qualifier("sortByDateAsc") SortStrategy sortByDateAsc,
            @Qualifier("sortByPriority") SortStrategy sortByPriority,
            @Qualifier("sortBySender") SortStrategy sortBySender,
            @Qualifier("sortByUnread") SortStrategy sortByUnread,
            @Qualifier("sortBySubject") SortStrategy sortBySubject,
            @Qualifier("sortByBody") SortStrategy sortByBody
    ) {
        strategies.put("DATE_DESC", sortByDateDesc);
        strategies.put("DATE_ASC", sortByDateAsc);
        strategies.put("PRIORITY", sortByPriority);
        strategies.put("SENDER", sortBySender);
        strategies.put("UNREAD_FIRST", sortByUnread);
        strategies.put("SUBJECT", sortBySubject);
        strategies.put("BODY", sortByBody);
    }

    public SortStrategy getStrategy(String strategyName) {
        SortStrategy strategy = strategies.get(strategyName.toUpperCase());
        if (strategy == null) {
            // Default to DATE_DESC if strategy not found
            return strategies.get("DATE_DESC");
        }
        return strategy;
    }

    public Map<String, SortStrategy> getAllStrategies() {
        return new HashMap<>(strategies);
    }
}