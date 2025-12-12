package com.mailapp.mailbackend.service.search.handlers;

import com.mailapp.mailbackend.dto.SearchCriteria;
import com.mailapp.mailbackend.entity.UserMail;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Component
public class DateRangeSearchHandler extends SearchHandler {

    @Override
    protected boolean shouldHandle(SearchCriteria criteria) {
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

        Predicate predicate = cb.between(
                root.get("sentAt"),
                startDate,
                endDate
        );

        predicates.add(predicate);
    }
}