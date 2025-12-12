
package com.mailapp.mailbackend.service.search.handlers;

import com.mailapp.mailbackend.dto.SearchCriteria;
import com.mailapp.mailbackend.entity.UserMail;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BodySearchHandler extends SearchHandler {

    @Override
    protected boolean shouldHandle(SearchCriteria criteria) {
        return criteria.getBody() != null && !criteria.getBody().trim().isEmpty();
    }

    @Override
    public void handle(
            SearchCriteria criteria,
            Root<UserMail> root,
            CriteriaBuilder cb,
            List<Predicate> predicates
    ) {
        String bodyTerm = "%" + criteria.getBody().toLowerCase() + "%";
        Join<Object, Object> mailJoin = root.join("mail", JoinType.LEFT);

        Predicate predicate = cb.like(
                cb.lower(mailJoin.get("body")),
                bodyTerm
        );

        predicates.add(predicate);
    }
}