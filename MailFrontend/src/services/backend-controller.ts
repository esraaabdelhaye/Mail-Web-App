import { Injectable } from '@angular/core';
import {Email, Folder} from '../app/models/email.model';

@Injectable({
  providedIn: 'root',
})
export class BackendController {
  getEmails(): Email[]{
    return [
      {
        id: '1',
        subject: 'Lab Assignment 4',
        body: 'Don\'t forget the JSON requirement!',
        sender: { name: 'Dr. Khaled', email: 'prof@alex.edu' },
        to: ['me@alex.edu'],
        date: new Date(),
        isRead: false,
        priority: 1,
        folder: 'inbox',
        attachments: []
      }
    ];
  }

  getFolders(): Folder[]{
    return [
      { id: 'inbox', name: 'Inbox', isCustom: false },
      { id: 'sent', name: 'Sent', isCustom: false },
      { id: 'trash', name: 'Trash', isCustom: false },
      { id: 'project', name: 'CSE 223 Project', isCustom: true }
    ];
  }
}
