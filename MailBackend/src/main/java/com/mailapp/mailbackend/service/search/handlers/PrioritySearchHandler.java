package com.mailapp.mailbackend.service.search.handlers;

import com.mailapp.mailbackend.dto.SearchCriteria;
import com.mailapp.mailbackend.entity.UserMail;
import com.mailapp.mailbackend.enums.Priority;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PrioritySearchHandler extends SearchHandler {

    @Override
    protected boolean shouldHandle(SearchCriteria criteria) {
        System.out.println("PrioritySearchHandler");
        return criteria.getPriority() != 0;
    }

    @Override
    public void handle(
            SearchCriteria criteria,
            Root<UserMail> root,
            CriteriaBuilder cb,
            List<Predicate> predicates
    ) {

        Predicate predicate = cb.equal(
                root.get("importance"),
                Priority.fromValue(criteria.getPriority())
        );

        predicates.add(predicate);
    }
}