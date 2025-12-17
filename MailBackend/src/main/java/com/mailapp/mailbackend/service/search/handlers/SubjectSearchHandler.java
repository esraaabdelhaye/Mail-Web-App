package com.mailapp.mailbackend.service.search.handlers;

import com.mailapp.mailbackend.dto.SearchCriteria;
import com.mailapp.mailbackend.entity.UserMail;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SubjectSearchHandler extends SearchHandler {

    @Override
    protected boolean shouldHandle(SearchCriteria criteria) {
        System.out.println("SubjectSearchHandler");
        return criteria.getSubject() != null && !criteria.getSubject().trim().isEmpty();
    }

    @Override
    public void handle(
            SearchCriteria criteria,
            Root<UserMail> root,
            CriteriaBuilder cb,
            List<Predicate> predicates
    ) {
        String subjectTerm = "%" + criteria.getSubject().toLowerCase() + "%";
        Join<Object, Object> mailJoin = root.join("mail", JoinType.LEFT);

        Predicate predicate = cb.like(
                cb.lower(mailJoin.get("subject")),
                subjectTerm
        );

        predicates.add(predicate);
    }
}