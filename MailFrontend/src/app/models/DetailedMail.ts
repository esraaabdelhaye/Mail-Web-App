import { SenderDTO } from './SenderDTO';
export interface AttachmentDTO {
  id: number;
  fileName: string;
  fileSize: string; // Formatted size like "1.2 MB"
  fileType: string; // MIME type
}

export interface MailDetailsDTO {
  id: number;
  sender: SenderDTO;

  // Java List<String> maps to string array
  to: string[];
  cc: string[];
  bcc: string[];

  subject: string;
  body: string;

  sentAt: string;
  isRead: boolean;
  priority: number;
  folder: string;

  // List of nested attachments
  attachments: AttachmentDTO[];
}
