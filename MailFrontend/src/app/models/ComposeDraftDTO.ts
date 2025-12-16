import { Recipient } from "../../features/compose-email/compose-email";
import { AttachmentDTO } from "./DetailedMail";

export interface ComposeDraftDTO {
  draftId: number;
  to: Recipient[];
  cc: Recipient[];
  bcc: Recipient[];
  subject: string;
  body: string;
  priority: number;
  attachments: AttachmentDTO[];
}