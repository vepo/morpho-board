import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { CreateTicketModalComponent } from './create-ticket-modal.component';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { CreateTicketRequest, Ticket, TicketService } from '../../services/ticket.service';
import { Project, ProjectsService } from '../../services/projects.service';
import { Category, CategoryService } from '../../services/category.service';
import { AuthService } from '../../services/auth.service';
import { of, throwError } from 'rxjs';

describe('CreateTicketModalComponent', () => {
    let component: CreateTicketModalComponent;
    let fixture: ComponentFixture<CreateTicketModalComponent>;
    let mockDialogRef: jasmine.SpyObj<MatDialogRef<CreateTicketModalComponent>>;
    let mockTicketService: jasmine.SpyObj<TicketService>;
    let mockCategoryService: jasmine.SpyObj<CategoryService>;
    let mockProjectsService: jasmine.SpyObj<ProjectsService>;
    let mockAuthService: jasmine.SpyObj<AuthService>;

    const mockProjects: Project[] = [
        { id: 1, name: 'Project 1', description: 'Desc 1' },
        { id: 2, name: 'Project 2', description: 'Desc 2' }
    ];

    const mockCategories: Category[] = [
        { id: 1, name: 'Bug' },
        { id: 2, name: 'Feature' },
        { id: 3, name: 'Improvement' }
    ];

    beforeEach(waitForAsync(() => {
        mockDialogRef = jasmine.createSpyObj('MatDialogRef', ['close']);
        mockTicketService = jasmine.createSpyObj('TicketService', ['createTicket']);
        mockCategoryService = jasmine.createSpyObj('CategoryService', ['findAll']);
        mockProjectsService = jasmine.createSpyObj('ProjectsService', ['findAll']);
        mockAuthService = jasmine.createSpyObj('AuthService', ['getAuthUserId']);
        mockAuthService.getAuthUserId.and.returnValue(123);

        TestBed.configureTestingModule({
            imports: [
                FormsModule,
                MatFormFieldModule,
                MatInputModule,
                MatButtonModule,
                MatSelectModule,
                NoopAnimationsModule,
                CreateTicketModalComponent
            ],
            providers: [
                { provide: MatDialogRef, useValue: mockDialogRef },
                { provide: MAT_DIALOG_DATA, useValue: {} },
                { provide: TicketService, useValue: mockTicketService },
                { provide: CategoryService, useValue: mockCategoryService },
                { provide: ProjectsService, useValue: mockProjectsService },
                { provide: AuthService, useValue: mockAuthService }
            ]
        }).compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(CreateTicketModalComponent);
        component = fixture.componentInstance;

        mockAuthService.getAuthUserId.and.returnValue(123);
        mockCategoryService.findAll.and.returnValue(of(mockCategories));
        mockProjectsService.findAll.and.returnValue(of(mockProjects));

        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should initialize with empty form and load data', () => {
        expect(component.title).toBe('');
        expect(component.description).toBe('');
        expect(component.category).toEqual(component.emptyCategory);
        expect(component.projectId).toBeNull();

        expect(mockCategoryService.findAll).toHaveBeenCalled();
        expect(mockProjectsService.findAll).toHaveBeenCalled();
        expect(mockAuthService.getAuthUserId).toHaveBeenCalled();

        expect(component.categories).toEqual(mockCategories);
        expect(component.projects).toEqual(mockProjects);
        expect(component.authorId).toBe(123);
    });

    it('should close the dialog when cancel is called', () => {
        component.close();
        expect(mockDialogRef.close).toHaveBeenCalled();
    });

    describe('createTicket', () => {
        it('should not create ticket if form is invalid', () => {
            component.createTicket();
            expect(mockTicketService.createTicket).not.toHaveBeenCalled();
            expect(component.error).toBe('Preencha todos os campos obrigatórios');
        });

        it('should create ticket when form is valid', () => {
            // Set valid form values
            component.projectId = 1;
            component.title = 'Test Ticket';
            component.description = 'Test Description';
            component.category = mockCategories[0];

            const mockResponse: Ticket = { id: 1, title: 'Test Ticket', description: 'Test Description', author: -1, project: -1, status: -1 };
            mockTicketService.createTicket.and.returnValue(of(mockResponse));

            component.createTicket();

            const expectedRequest: CreateTicketRequest = {
                title: 'Test Ticket',
                description: 'Test Description',
                categoryId: 1,
                authorId: 123,
                projectId: 1
            };

            expect(mockTicketService.createTicket).toHaveBeenCalledWith(expectedRequest);
            expect(mockDialogRef.close).toHaveBeenCalledWith(mockResponse);
        });

        // it('should handle error when ticket creation fails', () => {
        //     // Set valid form values
        //     component.projectId = 1;
        //     component.title = 'Test Ticket';
        //     component.description = 'Test Description';
        //     component.category = mockCategories[0];

        //     mockTicketService.createTicket.and.returnValue(throwError(() => new Error('Error')));

        //     component.createTicket();

        //     expect(component.error).toBe('Erro ao criar ticket');
        //     expect(component.loading).toBeFalse();
        // });
    });

    describe('template', () => {
        it('should render the modal title', () => {
            const title = fixture.nativeElement.querySelector('h2');
            expect(title.textContent).toContain('Criar Ticket');
        });

        // it('should render project select with options', () => {
        //     fixture.detectChanges(); // Ensure async data is rendered

        //     const select = fixture.nativeElement.querySelector('mat-select[name="projectId"]');
        //     expect(select).toBeTruthy();

        //     const options = fixture.nativeElement.querySelectorAll('mat-option');
        //     expect(options.length).toBe(mockProjects.length + 1); // +1 for disabled option
        // });

        // it('should render category select with options', () => {
        //     fixture.detectChanges(); // Ensure async data is rendered

        //     const select = fixture.nativeElement.querySelector('mat-select[name="categoryId"]');
        //     expect(select).toBeTruthy();
        //     console.log(select);

        //     const options = fixture.nativeElement.querySelectorAll('mat-option');
        //     expect(options.length).toBe(mockCategories.length + 1); // +1 for disabled option
        // });

        it('should render title and description inputs', () => {
            const titleInput = fixture.nativeElement.querySelector('input[name="title"]');
            expect(titleInput).toBeTruthy();

            const descTextarea = fixture.nativeElement.querySelector('textarea[name="description"]');
            expect(descTextarea).toBeTruthy();
        });

        it('should disable create button when form is invalid', () => {
            const createButton = fixture.nativeElement.querySelector('button:not(.cancel)');
            expect(createButton.disabled).toBeTrue();
        });

        it('should enable create button when form is valid', () => {
            // Set valid form values
            component.projectId = 1;
            component.title = 'Test Ticket';
            component.description = 'Test Description';
            component.category = mockCategories[0];

            fixture.detectChanges();

            const createButton = fixture.nativeElement.querySelector('button:not(.cancel)');
            expect(createButton.disabled).toBeFalse();
        });
    });
});