import { SenderDTO } from './SenderDTO';
export interface MailSummaryDTO {
  id: number;
  sender: SenderDTO;

  to: string[];

  subject: string;
  sentAt: string;
  preview: string;

  isRead: boolean;

  priority: number;

  hasAttachments: boolean;
}
