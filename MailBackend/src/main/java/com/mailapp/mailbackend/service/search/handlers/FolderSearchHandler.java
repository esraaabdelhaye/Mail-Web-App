package com.mailapp.mailbackend.service.search.handlers;

import com.mailapp.mailbackend.dto.SearchCriteria;
import com.mailapp.mailbackend.entity.UserMail;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FolderSearchHandler extends SearchHandler {

    @Override
    protected boolean shouldHandle(SearchCriteria criteria) {
        return criteria.getFolder() != null && !criteria.getFolder().trim().isEmpty();
    }

    @Override
    public void handle(
            SearchCriteria criteria,
            Root<UserMail> root,
            CriteriaBuilder cb,
            List<Predicate> predicates
    ) {

        Join<Object, Object> folderJoin = root.join("folder", JoinType.LEFT);

        Predicate predicate = cb.equal(
                folderJoin.get("folderName"),
                criteria.getFolder()
        );

        predicates.add(predicate);
    }
}