package com.mailapp.mailbackend.service.search.handlers;

import com.mailapp.mailbackend.dto.SearchCriteria;
import com.mailapp.mailbackend.entity.UserMail;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Component
public class DateRangeSearchHandler extends SearchHandler {

    // Removed the DataSourceTransactionManager as it was causing the boot error
    // and is not used in this logic.
    public DateRangeSearchHandler() {
        super();
    }

    @Override
    protected boolean shouldHandle(SearchCriteria criteria) {
        System.out.println("DateRangeSearchHandler");
        return criteria.getStartDate() != null && criteria.getEndDate() != null;
    }

    @Override
    public void handle(
            SearchCriteria criteria,
            Root<UserMail> root,
            CriteriaBuilder cb,
            List<Predicate> predicates
    ) {
        LocalDateTime startDateTime = criteria.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = criteria.getEndDate().atTime(23, 59, 59);

        Date startDate = Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());

        // We join with "mail" to access its nested properties safely
        Join<UserMail, Object> mailJoin = root.join("mail", JoinType.LEFT);

        if ("Drafts".equalsIgnoreCase(criteria.getFolder())) {
            System.out.println("from if condition");
            predicates.add(cb.between(
                    mailJoin.get("updatedAt"),
                    startDate,
                    endDate
            ));
        } else {
            System.out.println("from else condition");
            // Check where 'sentAt' lives.
            // If it's in the Mail entity, use mailJoin.get("sentAt").
            // If it's directly in UserMail, use root.get("sentAt").
            predicates.add(cb.between(
                    mailJoin.get("sentAt"),
                    startDate,
                    endDate
            ));
        }
    }
}