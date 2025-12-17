export interface SearchRequestDTO {
  query: string;
  from: string;
  to: string;
  subject: string;
  body: string; // Maps to 'Includes words'
  hasAttachment: boolean | null;
  folder: string | null;
  startDate: string; // Maps to Date input
  endDate: string;
  priority: number; // Maps to Priority dropdown
  isRead: boolean | null;
}
