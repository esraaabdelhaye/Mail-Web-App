
package com.mailapp.mailbackend.service.search.handlers;

import com.mailapp.mailbackend.dto.SearchCriteria;
import com.mailapp.mailbackend.entity.UserMail;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GlobalSearchHandler extends SearchHandler {

    @Override
    protected boolean shouldHandle(SearchCriteria criteria) {
        // if the search criteria received from the front-end contains a query attribute then the global search handlers will handle it
        return criteria.getQuery() != null && !criteria.getQuery().trim().isEmpty();
    }

    @Override
    public void handle(
            SearchCriteria criteria, // from the front-end
            Root<UserMail> root,
            CriteriaBuilder cb,
            List<Predicate> predicates
    ) {

        // match anything containing the (keyword provided by user) anywhere
        String searchTerm = "%" + criteria.getQuery().toLowerCase() + "%";

        // Join with Mail entity to search in from/subject/body
        Join<Object, Object> mailJoin = root.join("mail", JoinType.LEFT);
        Join<Object, Object> senderJoin = mailJoin.join("sender", JoinType.LEFT);

        // the from-predicate represents the query that searches the sender email if it contains the keyword anywhere
        Predicate fromPredicate = cb.like(
                cb.lower(senderJoin.get("email")),
                searchTerm
        );

        // the subject-predicate represents the query that searches the subject if it contains the keyword anywhere
        Predicate subjectPredicate = cb.like(
                cb.lower(mailJoin.get("subject")),
                searchTerm
        );

        // the body-predicate represents the query that searches the email body if it contains the keyword anywhere
        Predicate bodyPredicate = cb.like(
                cb.lower(mailJoin.get("body")),
                searchTerm
        );

        // combine all prev predicates
        Predicate globalPredicate = cb.or(
                fromPredicate,
                subjectPredicate,
                bodyPredicate
        );

        predicates.add(globalPredicate);
    }
}