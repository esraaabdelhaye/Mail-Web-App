package com.mailapp.mailbackend.service.search.handlers;

import com.mailapp.mailbackend.dto.SearchCriteria;
import com.mailapp.mailbackend.entity.MailReceiver;
import com.mailapp.mailbackend.entity.User;
import com.mailapp.mailbackend.entity.UserMail;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ToSearchHandler extends SearchHandler {

    @Override
    protected boolean shouldHandle(SearchCriteria criteria) {
        return criteria.getTo() != null && !criteria.getTo().trim().isEmpty();
    }

    @Override
    public void handle(
            SearchCriteria criteria,
            Root<UserMail> root,
            CriteriaBuilder cb,
            List<Predicate> predicates
    ) {
        String searchTerm = "%" + criteria.getTo().toLowerCase() + "%";

        Subquery<Long> subquery = cb.createQuery().subquery(Long.class);
        Root<MailReceiver> subRoot = subquery.from(MailReceiver.class);

        // Join from MailReceiver to User (the receiver)
        Join<MailReceiver, User> receiverJoin = subRoot.join("receiver", JoinType.INNER);

        // Predicate 1: Match Email
        Predicate emailMatch = cb.like(cb.lower(receiverJoin.get("email")), searchTerm);

        // Predicate 2: Match Name (Assuming your User entity has a 'name' or 'fullName' field)
        Predicate nameMatch = cb.like(cb.lower(receiverJoin.get("fullName")), searchTerm);

        subquery.select(subRoot.get("id"))
                .where(
                        cb.equal(subRoot.get("mail"), root.get("mail")), // Link back to the mail being processed
                        cb.or(emailMatch, nameMatch) // Match EITHER email OR name
                );

        predicates.add(cb.exists(subquery));
    }
}