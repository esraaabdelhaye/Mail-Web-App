package com.mailapp.mailbackend.repository;

import com.mailapp.mailbackend.dto.SearchCriteria;
import com.mailapp.mailbackend.entity.Mail;
import com.mailapp.mailbackend.entity.User;
import com.mailapp.mailbackend.entity.UserMail;
import com.mailapp.mailbackend.service.search.handlers.SearchHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class EmailSearchRepository {

    @PersistenceContext
    private EntityManager entityManager;


    public Page<UserMail> search(
            Long userId,
            SearchCriteria criteria,
            Pageable pageable,
            SearchHandler searchChain     // this will be the head of the search chain which will always be the global search handler
    ) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder(); // Used to build our search query

        CriteriaQuery<UserMail> query = cb.createQuery(UserMail.class); // Our Query Object
        Root<UserMail> root = query.from(UserMail.class); // Used to make the query sepecific to a certain user


        // These are our criteria like which folder it belongs to, does the email has attachments or not,...
        List<Predicate> predicates = new ArrayList<>();

        // Add the first predicate to be that user id matches the registered user
        predicates.add(cb.equal(root.get("user").get("id"), userId));

        // predicates will be added to the list using the handlers chain
        searchChain.processChain(criteria, root, cb, predicates);

        // Combine all predicates with AND
        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        // Apply sorting from Pageable
        Join<UserMail, Mail> mailJoin = root.join("mail", JoinType.LEFT);
        Join<UserMail, User> userJoin = root.join("user", JoinType.LEFT);


        // Combine all predicates with AND
        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        // Replace your snippet with this logic
        if (pageable.getSort().isSorted()) {
            List<Order> jpaOrders = new ArrayList<>();

            pageable.getSort().forEach(order -> {
                String property = order.getProperty();
                Path<?> path;

                // Manually resolve nested paths like "mail.subject"
                if (property.contains(".")) {
                    String[] parts = property.split("\\.");
                    path = root.get(parts[0]);
                    for (int i = 1; i < parts.length; i++) {
                        path = path.get(parts[i]);
                    }
                } else {
                    path = root.get(property);
                }

                // Convert Spring Order to JPA Order
                if (order.isAscending()) {
                    jpaOrders.add(cb.asc(path));
                } else {
                    jpaOrders.add(cb.desc(path));
                }
            });

            query.orderBy(jpaOrders);
        }
        // Execute query with pagination
        TypedQuery<UserMail> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<UserMail> results = typedQuery.getResultList();

        // Count query for total elements
        long total = countResults(userId, criteria, searchChain, cb);

        return new PageImpl<>(results, pageable, total);
    }

    /**
     * Count total results (for pagination)
     */
    private long countResults(
            Long userId,
            SearchCriteria criteria,
            SearchHandler searchChain,
            CriteriaBuilder cb
    ) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<UserMail> countRoot = countQuery.from(UserMail.class);

        // Apply same predicates
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(countRoot.get("user").get("id"), userId));
        searchChain.processChain(criteria, countRoot, cb, predicates);

        countQuery.select(cb.count(countRoot));

        if (!predicates.isEmpty()) {
            countQuery.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }
}