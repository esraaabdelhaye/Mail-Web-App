import { Component, ChangeDetectionStrategy, signal, OnInit, OnDestroy, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EmailHandler } from '../../services/emails-handler/email-handler';
import { AuthService } from '../../services/auth/auth-service';
import { EmailPageDTO } from '../../app/models/EmailPageDTO';
import { Observable, Subscription } from 'rxjs';
import { PaginationRequest } from '../../app/models/PaginationRequest';
import {
  LucideAngularModule,
  Search,
  RefreshCw,
  Trash2,
  FolderInput,
  ArrowUpDown,
  Flag,
  AlertCircle,
  ChevronLeft,
  ChevronRight,
  ChevronsUp,
  ChevronsDown,
  LayoutList,
  LayoutGrid,
} from 'lucide-angular';
import { ButtonComponent } from '../../shared/button/button';
import { MailSummaryDTO } from '../../app/models/MailSummaryDTO';
import { MailDetailsDTO } from '../../app/models/DetailedMail';
import { SearchOptionsModalComponent } from '../search-options-modal/search-options-modal';
import { SearchRequestDTO } from '../../app/models/SearchRequestDTO';
import { ComposeEmail } from './../compose-email/compose-email';

@Component({
  selector: 'app-email-list',
  standalone: true,
  imports: [
    CommonModule,
    LucideAngularModule,
    FormsModule,
    ButtonComponent,
    SearchOptionsModalComponent,
    ComposeEmail,
  ],
  templateUrl: './email-list.html',
  styleUrls: ['./email-list.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmailListComponent implements OnInit, OnDestroy {
  // --- Icons for Template ---
  readonly icons = {
    Search,
    RefreshCw,
    Trash2,
    FolderInput,
    ArrowUpDown,
    Flag,
    AlertCircle,
    ChevronLeft,
    ChevronRight,
    LayoutList,
    LayoutGrid,
    ChevronsUp,
    ChevronsDown,
  };

  public emailPage = signal<EmailPageDTO | null>(null);
  public paginatedEmails = signal<MailSummaryDTO[]>([]);
  public processedEmails = signal<MailSummaryDTO[]>([]);

  public searchQuery = signal('');
  public sortBy = signal('DATE_DESC');
  public viewMode = signal<'default' | 'priority'>('default');

  public selectedIds = signal(new Set<Number>());
  public isLoading = signal(false);
  public isRefreshing = signal<boolean>(false);
  public selectedEmailId = signal<number | null>(null);
  public currentlyOpened = signal<MailDetailsDTO | null>(null);

  public currentPage = signal(1);
  public itemsPerPage = signal(10);
  public isFirst = signal<boolean>(true);
  public isLast = signal<boolean>(true);

  public totalPages = computed(() => this.emailPage()?.totalPages || 1);
  public totalEmails = computed(() => this.emailPage()?.totalElements || 0);

  private pollingInterval: any;

  public onRefresh(): void {
    this.fetchMail(true);
  }
  public openEmail(emailId: number): void {
    if (this.isDraftsFolder()) {
      // this.selectedEmailId.set(emailId);
      console.log(emailId);

      this.openDraftInCompose(emailId);
      return;
    }
    this.selectedEmailId.set(emailId);
    const userId = this.authService.getCurrentUserId();
    this.emailHandler.getMailDetails(Number(userId), emailId).subscribe({
      next: (data) => {
        this.currentlyOpened.set(data);
        console.log('Email details received:', data);
        console.log('Attachments:', data.attachments);
        console.log('Attachments length:', data.attachments?.length);
        console.log('from email List: ', this.currentlyOpened);
      },
      error: (err) => {
        console.log('failed to open email', err);
      },
    });
  }

  private openDraftInCompose(emailId: number): void {
    const userId = this.authService.getCurrentUserId();

    this.emailHandler.getDraftForCompose(emailId).subscribe({
      next: (draft) => {
        this.emailHandler.openComposeWithDraft(draft);
      },
      error: (err) => {
        console.log('failed to open draft', err);
      },
    });
  }

  private isDraftsFolder(): boolean {
    return this.emailHandler.currentFolderName() === 'Drafts';
  }

  public closeOpenedEmail() {
    this.selectedEmailId.set(null);
    this.currentlyOpened.set(null);
  }

  public math = Math;

  constructor(protected emailHandler: EmailHandler, private authService: AuthService) {}

  ngOnInit(): void {
    // this is very very very very very very very very very very bad design, this should be changed but i'll leave it cause I only want to test the concept
    // The problem is due to fetchMail() being in this component only, and I need to call it from emailHandler
    this.emailHandler.regList(this);
    // this.sortBy.set('DATE_DESC');
    this.fetchMail();

    // Set up polling to refresh emails every 10 seconds
    this.pollingInterval = setInterval(() => {
      this.fetchMail();
    }, 10000); // 10 seconds
  }

  ngOnDestroy(): void {
    // Clean up the interval when component is destroyed
    if (this.pollingInterval) {
      clearInterval(this.pollingInterval);
    }
  }

  priorityChanged() {
    if (this.searchQuery() == '') {
      this.fetchMail();
    } else this.onSearchChange(this.searchQuery());
  }

  public fetchMail(isRefresh: boolean = false): void {
    this.isLoading.set(true);
    if (isRefresh) this.isRefreshing.set(true);

    const apiPage = this.currentPage() - 1;
    const userId = this.authService.getCurrentUserId();
    const request: PaginationRequest = {
      userId: Number(userId),
      folderName: this.emailHandler.currentFolderName(),
      page: apiPage,
      size: this.itemsPerPage(),
      sortBy: this.sortBy(),
      // sortDirection: 'desc',
    };

    this.emailHandler.getMailPage(request).subscribe({
      next: (data) => {
        // Set the main page data
        this.emailPage.set(data);
        console.log('here emails: ', data.content);
        this.paginatedEmails.set(data.content);

        this.currentPage.set(data.currentPage + 1);
        this.isFirst.set(data.isFirst);
        this.isLast.set(data.isLast);

        this.isLoading.set(false);
        this.isRefreshing.set(false);

        this.emailHandler.loadFolderCounts();
      },
      error: (err) => {
        console.error('Failed to load emails:', err);
        this.isLoading.set(false);
        this.isRefreshing.set(false);
      },
    });
  }

  changePage(delta: number): void {
    const newPage = this.currentPage() + delta;

    if (newPage >= 1 && newPage <= this.totalPages()) {
      this.currentPage.set(newPage);
      this.fetchMail();
    }
  }

  toggleEmailSelection(id: Number, event: Event): void {
    const isChecked = (event.target as HTMLInputElement).checked;
    this.selectedIds.update((set) => {
      if (isChecked) {
        set.add(id);
      } else {
        set.delete(id);
      }
      return new Set(set); // Return a new set instance for change detection (since Set is mutable)
    });
  }

  toggleSelectAll(event: Event): void {
    const isChecked = (event.target as HTMLInputElement).checked;

    this.selectedIds.update((set) => {
      if (isChecked) {
        // Select all currently loaded emails
        this.paginatedEmails().forEach((email) => set.add(email.id));
      } else {
        // Deselect all
        set.clear();
      }
      return new Set(set);
    });
  }

  handleAdvancedSearch(request: SearchRequestDTO) {
    this.emailHandler.doAdvancedSearch(request).subscribe({
      next: (data) => {
        this.emailPage.set(data);
        this.paginatedEmails.set(data.content);

        // Reset Pagination
        this.currentPage.set(1);

        // this.currentFolder.set('Search Results');

        this.isLoading.set(false);
        console.log('Search result: ', data);
      },
      error: (err) => {
        console.log('Advanced search failed, Error: ');
        console.log(err);
      },
    });
  }

  // --- BULK ACTION HANDLERS ---

  handleMove(targetFolderName: string): void {
    const mailIds = Array.from(this.selectedIds());

    console.log(`Moving ${mailIds.length} emails to folder: ${targetFolderName}`);
    this.emailHandler.moveEmailsToFolder(
      mailIds,
      targetFolderName,
      'Emails moved successfully',
      () => {
        this.selectedIds.set(new Set());

        this.onRefresh();
      }
    );
  }

  handleDelete(): void {
    const mailIds = Array.from(this.selectedIds());

    if (this.emailHandler.currentFolderName() == 'Trash') {
      if (
        confirm(`Are you sure you want to PERMANENTLY delete ${this.selectedIds().size} emails?`)
      ) {
        this.emailHandler.permanentlyDeleteEmails(mailIds, () => {
          this.selectedIds.set(new Set());
          this.onRefresh();
        });
      }
    } else {
      if (confirm(`Are you sure you want to delete ${this.selectedIds().size} emails?`)) {
        // console.log(`Deleting ${this.selectedIds().size} emails...`);
        // this.selectedIds.set(new Set());
        // this.fetchMail();
        this.emailHandler.moveEmailsToFolder(
          mailIds,
          'Trash',
          'Emails deleted to Trash successfully',
          () => {
            this.selectedIds.set(new Set());

            this.onRefresh();
          }
        );
      }
    }
  }

  // Quick Search
  onSearchChange(query: string) {
    const apiPage = this.currentPage() - 1;
    const userId = this.authService.getCurrentUserId();

    const request: PaginationRequest = {
      userId: Number(userId),
      folderName: this.emailHandler.currentFolderName(),
      page: apiPage,
      size: this.itemsPerPage(),
      sortBy: this.sortBy(),
    };

    this.emailHandler.getQuickSearchResults(request, query).subscribe({
      next: (data) => {
        this.emailPage.set(data);
        console.log('here are the search results ', data.content);
        this.paginatedEmails.set(data.content);

        this.currentPage.set(data.currentPage + 1);
        this.isFirst.set(data.isFirst);
        this.isLast.set(data.isLast);

        this.isLoading.set(false);
        this.isRefreshing.set(false);
      },
      error: (err) => {
        console.error('Failed to search emails:', err);
        this.isLoading.set(false);
        this.isRefreshing.set(false);
      },
    });
  }

  // --- UTILITIES (Referenced in HTML) ---

  formatDate(dateString: string): string {
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
    } catch (e) {
      return dateString;
    }
  }
}
