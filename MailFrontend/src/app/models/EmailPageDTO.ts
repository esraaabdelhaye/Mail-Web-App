import { Email } from './email.model';

export interface EmailPageDTO {
  content: Email[];
  totalPages: number;
  totalElements: number;
  currentPage: number;
  isFirst: boolean;
  isLast: boolean;
  pageSize: number;
}
