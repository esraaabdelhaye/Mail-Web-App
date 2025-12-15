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
            @Qualifier("sortByBody") SortStrategy sortByBody,
            @Qualifier("sortByUpdatedDateDesc") SortStrategy sortByUpdatedDateDesc,
            @Qualifier("sortByUpdatedDateAsc") SortStrategy sortByUpdatedDateAsc

    ) {
        strategies.put("DATE_DESC", sortByDateDesc);
        strategies.put("DATE_ASC", sortByDateAsc);
        strategies.put("PRIORITY", sortByPriority);
        strategies.put("SENDER", sortBySender);
        strategies.put("UNREAD_FIRST", sortByUnread);
        strategies.put("SUBJECT", sortBySubject);
        strategies.put("BODY", sortByBody);
        strategies.put("UPDATED_DATE_DESC", sortByUpdatedDateDesc);
        strategies.put("UPDATED_DATE_ASC", sortByUpdatedDateAsc);
    }

    public SortStrategy getStrategy(String strategyName, String folderName) {

        if(folderName.equals("Drafts")){
            if(strategyName.equalsIgnoreCase("DATE_DESC")){
                strategyName = "UPDATED_DATE_DESC";
            }else if(strategyName.equalsIgnoreCase("DATE_ASC")){
                strategyName = "UPDATED_DATE_ASC";
            }
        }

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