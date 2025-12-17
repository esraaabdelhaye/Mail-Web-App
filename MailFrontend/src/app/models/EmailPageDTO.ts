import { MailSummaryDTO } from './MailSummaryDTO';
export interface EmailPageDTO {
  content: MailSummaryDTO[];
  totalPages: number;
  totalElements: number;
  currentPage: number;
  isFirst: boolean;
  isLast: boolean;
  pageSize: number;
}
