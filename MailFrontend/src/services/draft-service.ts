import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DraftAttachment {
  id?: number;
  file: File;
}

export interface DraftRecipient {
  id?: number;
  type: 'to' | 'cc' | 'bcc';
  email: string;
}

@Injectable({
  providedIn: 'root',
})
export class DraftService {
  private readonly apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  createDraft(): Observable<{ draftId: number }> {
    return this.http.post<{ draftId: number }>(`${this.apiUrl}/email/draft/create`, {});
  }

  autoSaveDraft(draftId: number, data: { subject: string; body: string; priority: number }) {
    return this.http.post(`${this.apiUrl}/email/draft`, { draftId, ...data }, { responseType: 'text' });
  }

  uploadAttachment(draftId: number, file: File) {
    const form = new FormData();
    form.append('draftId', draftId.toString());
    form.append('file', file);
    return this.http.post(`${this.apiUrl}/email/draft/attachment`, form);
  }

  deleteAttachment(draftId: number, fileName: string) {
    return this.http.delete(`${this.apiUrl}/email/draft/${draftId}/attachment/${encodeURIComponent(fileName)}`);
  }

  addRecipient(draftId: number, type: 'to' | 'cc' | 'bcc', email: string) {
    return this.http.post(`${this.apiUrl}/email/draft/recipient/add`, { draftId, type, email });
  }

  deleteRecipient(draftId: number, type: 'to' | 'cc' | 'bcc', email: string) {
    return this.http.delete(`${this.apiUrl}/email/draft/${draftId}/recipient/${type}/${encodeURIComponent(email)}`);
  }

  sendEmail(data: any, attachments: File[]) {
    const form = new FormData();
    form.append('email', new Blob([JSON.stringify(data)], { type: 'application/json' }));
    attachments.forEach((file) => form.append('files', file));
    return this.http.post(`${this.apiUrl}/email/send`, form, { responseType: 'text' as 'json' });
  }
}
