export interface Email {
  id: Number;
  sender: {
    name: string;
    email: string;
  };
  to: string[];
  subject: string;
  body: string;
  date: Date;
  isRead: boolean;
  priority: 1 | 2 | 3 | 4; // 1 = highest, 4 = lowest
  folder: 'inbox' | 'sent' | 'drafts' | 'trash' | string;
  attachments: Attachment[];
}

export interface Attachment {
  id: string;
  name: string;
  size: string;
  type: string;
}

export interface Contact {
  id: string;
  name: string;
  emails: string[];
}

export interface Folder {
  id: string;
  name: string;
  isCustom: boolean; // If false then this folder was created by our app and can't be edited by the user, if true then this folder was created by the user
}

export type SortOption = 'date' | 'sender' | 'importance' | 'subject';
export type ViewMode = 'default' | 'priority';
