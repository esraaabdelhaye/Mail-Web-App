export class UserDTO {
  id?: Number | null;
  fullName?: string;
  email!: string;
  password!: string;
  reqState?: Boolean;
  reqMessage?: string;
}
