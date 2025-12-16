package com.mailapp.mailbackend.service.Mail;

import com.mailapp.mailbackend.entity.Folder;
import com.mailapp.mailbackend.entity.User;
import com.mailapp.mailbackend.entity.UserMail;
import com.mailapp.mailbackend.repository.UserMailRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

@Service
public class PriorityMailService {
    @Autowired
    private UserMailRepo userMailRepo;

    public Page<UserMail> getPriorityInboxPage(
            User user,
            Folder folder,
            Pageable pageable
    ) {
        List<UserMail> mails =
                userMailRepo.findByUserAndFolder(user, folder);

        PriorityQueue<UserMail> pq = new PriorityQueue<>(
                (a, b) -> {
                    int p = Integer.compare(b.getImportance().getValue(), a.getImportance().getValue());
                    if (p != 0) return p;
                    return b.getMail().getSentAt()
                            .compareTo(a.getMail().getSentAt());
                }
        );

        pq.addAll(mails);

        List<UserMail> sorted = new ArrayList<>();
        while (!pq.isEmpty()) {
            sorted.add(pq.poll());
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), sorted.size());

        List<UserMail> pageContent =
                start >= sorted.size()
                        ? List.of()
                        : sorted.subList(start, end);

        return new PageImpl<>(
                pageContent,
                pageable,
                sorted.size()
        );
    }

}
