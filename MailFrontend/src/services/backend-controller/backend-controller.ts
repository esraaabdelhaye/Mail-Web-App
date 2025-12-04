import { Injectable } from '@angular/core';
import {Email, Folder} from '../../app/models/email.model';

@Injectable({
  providedIn: 'root',
})
export class BackendController {
  getEmails(): Email[]{
    return [
      // --- INBOX ---
      {
        id: '101',
        subject: 'Urgent: Lab 4 Requirements Update',
        body: 'Dear Students, please note that the JSON requirement is strict. You must ensure the schema is efficient. Also, the bonus marks for "eye-catching UI" are significant this year. Good luck!',
        sender: { name: 'Dr. Khaled Nagy', email: 'khaled.nagy@alex.edu.eg' },
        to: ['cse223-all@alex.edu.eg'],
        date: new Date(new Date().getTime() - 1000 * 60 * 30), // 30 mins ago
        isRead: false,
        priority: 1, // High
        folder: 'inbox',
        attachments: [
          { name: 'updated_requirements.pdf', id: 'f1', size: '1.2MB', type: 'application/pdf' }
        ]
      },
      {
        id: '102',
        subject: 'Question about the Builder Pattern',
        body: 'Hey Kimo, I was stuck on the EmailBuilder part. Do we need to implement the Director as well, or just the Builder? Let me know if you want to study together at the library later.',
        sender: { name: 'Ahmed (Teammate)', email: 'ahmed@student.alex.edu' },
        to: ['me@alex.edu.eg'],
        date: new Date(new Date().getTime() - 1000 * 60 * 60 * 2), // 2 hours ago
        isRead: false,
        priority: 2, // Medium
        folder: 'inbox',
        attachments: []
      },
      {
        id: '103',
        subject: 'Library Book Overdue',
        body: 'This is a reminder that your copy of "Clean Code" is due tomorrow. Please return it to the faculty library to avoid fines.',
        sender: { name: 'Faculty Library', email: 'library@alex.edu.eg' },
        to: ['me@alex.edu.eg'],
        date: new Date(new Date().getTime() - 1000 * 60 * 60 * 24), // Yesterday
        isRead: true,
        priority: 3, // Low
        folder: 'inbox',
        attachments: []
      },
      {
        id: '104',
        subject: 'Internship Opportunity: Software Engineer',
        body: 'We are looking for bright CSE students for our summer internship program. Apply with your CV attached.',
        sender: { name: 'Tech Corp HR', email: 'careers@techcorp.com' },
        to: ['students@alex.edu.eg'],
        date: new Date(new Date().setDate(new Date().getDate() - 2)), // 2 days ago
        isRead: true,
        priority: 2,
        folder: 'inbox',
        attachments: [
          { name: 'job_desc.pdf', id: 'f2', size: '500KB', type: 'application/pdf' }
        ]
      },

      // --- SENT ---
      {
        id: '201',
        subject: 'Re: Lab 4 Team Formation',
        body: 'Hello Eng. Ismail, our team is formed. It consists of Karim, Ahmed, Sarah, and Omar. We will be using Angular and Spring Boot.',
        sender: { name: 'Me', email: 'kimo@alex.edu.eg' },
        to: ['ismail.elyamany@alex.edu.eg'],
        date: new Date(new Date().getTime() - 1000 * 60 * 60 * 5), // 5 hours ago
        isRead: true,
        priority: 2,
        folder: 'sent',
        attachments: []
      },

      // --- PROJECT FOLDER ---
      {
        id: '301',
        subject: 'Frontend Mockups',
        body: 'Attached are the screenshots of the UI we want to build. I used Shadcn UI as discussed.',
        sender: { name: 'Sarah', email: 'sarah@student.alex.edu' },
        to: ['team@alex.edu.eg'],
        date: new Date(new Date().setDate(new Date().getDate() - 3)),
        isRead: true,
        priority: 2,
        folder: 'project',
        attachments: [
          { name: 'dashboard.png', id: 'f3', size: '2.4MB', type: 'image/png' },
          { name: 'sidebar.png', id: 'f4', size: '1.1MB', type: 'image/png' }
        ]
      },
      {
        id: '302',
        subject: 'Backend API Docs',
        body: 'I finished the Swagger documentation for the Email Controller. Please check the endpoints before starting the Angular service.',
        sender: { name: 'Omar', email: 'omar@student.alex.edu' },
        to: ['team@alex.edu.eg'],
        date: new Date(new Date().setDate(new Date().getDate() - 1)),
        isRead: false,
        priority: 1, // High
        folder: 'project',
        attachments: []
      },

      // --- TRASH ---
      {
        id: '901',
        subject: 'Spam: Win a free iPhone!',
        body: 'Click here to claim your prize now!!!',
        sender: { name: 'Lottery Winner', email: 'spam@suspicious.com' },
        to: ['me@alex.edu.eg'],
        date: new Date(new Date().setDate(new Date().getDate() - 10)),
        isRead: true,
        priority: 3,
        folder: 'trash',
        attachments: []
      }
    ]
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
