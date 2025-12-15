export interface SearchRequestDTO{
  query: string;
  from: string;
  to: string;
  subject: string;
  body: string;           // Maps to 'Includes words'
  hasAttachment: boolean;
  folder: string | null;
  startDate: string;         // Maps to Date input
  endDate: string;
  priority: string | null;       // Maps to Priority dropdown
  isRead: boolean;
}
