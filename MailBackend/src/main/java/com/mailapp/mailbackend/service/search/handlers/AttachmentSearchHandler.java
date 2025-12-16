package com.mailapp.mailbackend.service.search.handlers;

import com.mailapp.mailbackend.dto.SearchCriteria;
import com.mailapp.mailbackend.entity.UserMail;
import com.mailapp.mailbackend.entity.Mail;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AttachmentSearchHandler extends SearchHandler {

    @Override
    protected boolean shouldHandle(SearchCriteria criteria) {
        // Handle only if the user specifically requested to filter by attachment status
        return criteria.getHasAttachment() != null;
    }

    @Override
    public void handle(
            SearchCriteria criteria,
            Root<UserMail> root,
            CriteriaBuilder cb,
            List<Predicate> predicates
    ) {
        // Path navigation: UserMail -> Mail
        Join<UserMail, Mail> mailJoin = root.join("mail", JoinType.LEFT);

        if (Boolean.TRUE.equals(criteria.getHasAttachment())) {
            // Predicate to check if the attachments list is NOT empty
            System.out.println("here in the if statement");
            predicates.add(cb.isNotEmpty(mailJoin.get("attachments")));
        } else {
            // Predicate to check if the attachments list IS empty
            predicates.add(cb.isEmpty(mailJoin.get("attachments")));
        }

        System.out.println("Filtering by hasAttachment: " + criteria.getHasAttachment());
    }
}