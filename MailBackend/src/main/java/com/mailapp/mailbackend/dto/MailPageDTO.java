package com.mailapp.mailbackend.dto;

import lombok.*;

import java.util.List;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MailPageDTO {
    // List of emails for the current page
    private List<MailSummaryDTO> content;

    // Pagination metadata
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private boolean isFirst;
    private boolean isLast;
    private int pageSize;

    public void setIsFirst(boolean first) {
    }

    public void setIsLast(boolean last) {
    }
}