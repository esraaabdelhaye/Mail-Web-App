export interface PaginationRequest {
  userId: number;
  folderName: string;
  page: number; // 0-indexed page number
  size: number; // Items per page
  sortBy?: string; // e.g., 'sentAt'
  sortDirection?: 'asc' | 'desc'; // e.g., 'desc'
}
