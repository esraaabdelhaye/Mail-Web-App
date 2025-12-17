package com.mailapp.mailbackend.service.cleanup;

import com.mailapp.mailbackend.entity.UserMail;
import com.mailapp.mailbackend.repository.UserMailRepo;
import com.mailapp.mailbackend.service.Mail.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmailCleanupService {

    @Autowired
    private UserMailRepo userMailRepo;
    @Autowired
    private MailService mailService;


    //@Scheduled(cron = "0 * * * * *")
    @Scheduled(cron = "0 0 2 * * *")  // Run at 2 AM daily
    public void deleteOldTrashEmails() {
        log.info("Starting scheduled cleanup of old trash emails...");

        Calendar calendar = Calendar.getInstance();
        //calendar.add(Calendar.MINUTE, -1);
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        Date cutoffDate = calendar.getTime();

        // Find old emails in Trash
        List<UserMail> oldEmails = userMailRepo.findOldTrashEmails(cutoffDate);

        if (!oldEmails.isEmpty()) {
            log.info("Found {} old emails in Trash to delete", oldEmails.size());

            // Group by user and delete using the service method
            Map<Long, List<Long>> emailsByUser = oldEmails.stream()
                    .collect(Collectors.groupingBy(
                            um -> um.getUser().getId(),
                            Collectors.mapping(UserMail::getId, Collectors.toList())
                    ));

            for (Map.Entry<Long, List<Long>> entry : emailsByUser.entrySet()) {
                try {
                    mailService.permanentlyDeleteEmails(entry.getKey(), entry.getValue());
                    log.info("Deleted {} emails for user {}", entry.getValue().size(), entry.getKey());
                } catch (Exception e) {
                    log.error("Failed to delete emails for user {}: {}", entry.getKey(), e.getMessage());
                }
            }

            log.info("Cleanup completed");
        } else {
            log.info("No old trash emails to delete");
        }
    }
    

}
