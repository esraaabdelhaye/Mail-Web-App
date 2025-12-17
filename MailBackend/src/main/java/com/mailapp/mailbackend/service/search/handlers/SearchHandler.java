
package com.mailapp.mailbackend.service.search.handlers;

import com.mailapp.mailbackend.dto.SearchCriteria;
import com.mailapp.mailbackend.entity.UserMail;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;

public abstract class SearchHandler {
    protected SearchHandler nextHandler;

    public void setNext(SearchHandler handler) {
        this.nextHandler = handler;
    }


    public abstract void handle(
            SearchCriteria criteria,
            Root<UserMail> root,
            CriteriaBuilder cb,
            List<Predicate> predicates
    );

    protected abstract boolean shouldHandle(SearchCriteria criteria);

    // if the search request should be handled by this criteria then it will handle it and then pass it to the next criteria in chain
    // handling a criteria is by adding a predicate (query) to the predicates
    public void processChain(
            SearchCriteria criteria,
            Root<UserMail> root,
            CriteriaBuilder cb,
            List<Predicate> predicates
    ) {
        if (shouldHandle(criteria)) {
            handle(criteria, root, cb, predicates);
        }

        if (nextHandler != null) {
            nextHandler.processChain(criteria, root, cb, predicates);
        }
    }
}