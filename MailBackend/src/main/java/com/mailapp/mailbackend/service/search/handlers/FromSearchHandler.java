// src/main/java/com/mailapp/mailbackend/service/search/handlers/FromSearchHandler.java
package com.mailapp.mailbackend.service.search.handlers;

import com.mailapp.mailbackend.dto.SearchCriteria;
import com.mailapp.mailbackend.entity.UserMail;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FromSearchHandler extends SearchHandler {

    @Override
    protected boolean shouldHandle(SearchCriteria criteria) {
        return criteria.getFrom() != null && !criteria.getFrom().trim().isEmpty();
    }

    @Override
    public void handle(
            SearchCriteria criteria,
            Root<UserMail> root,
            CriteriaBuilder cb,
            List<Predicate> predicates
    ) {
        String fromTerm = "%" + criteria.getFrom().toLowerCase() + "%";
        Join<Object, Object> mailJoin = root.join("mail", JoinType.LEFT);
        Join<Object, Object> senderJoin = mailJoin.join("sender", JoinType.LEFT);

        Predicate predicate = cb.like(
                cb.lower(senderJoin.get("email")),
                fromTerm
        );
        System.out.println("from search succeeded: ");
        predicates.add(predicate);
    }
}