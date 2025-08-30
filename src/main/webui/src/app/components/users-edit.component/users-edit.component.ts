import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { UsersService } from '../../services/users.service';

@Component({
  selector: 'app-users-edit.component',
  imports: [ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatCheckboxModule, MatButtonModule],
  templateUrl: './users-edit.component.html',
  styleUrl: './users-edit.component.scss',
  standalone: true
})
export class UsersEditComponent implements OnInit {
  editMode: boolean = false;
  userId: number | null = null;
  userForm = new FormGroup({
    username: new FormControl('', [Validators.required, Validators.minLength(4), Validators.maxLength(15)]),
    name: new FormControl('', [Validators.required]),
    email: new FormControl('', [Validators.email, Validators.required]),
    roles: new FormControl([] as string[], Validators.required)
  });


  constructor(private readonly activatedRoute: ActivatedRoute,
    private readonly usersService: UsersService,
    private readonly router: Router) {
  }

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ user }) => {
      console.debug("User data: ", user);
      this.editMode = user != null;
      this.userId = user.id;
      this.userForm.setValue({
        username: user.username,
        name: user.name,
        email: user.email,
        roles: user.roles
      });
    });
  }


  toggle(role: string): void {
    const currentRoles = this.userForm.get('roles')?.value || [];
    const roleIndex = currentRoles.indexOf(role);

    let newRoles: string[];
    if (roleIndex === -1) {
      newRoles = [...currentRoles, role];
    } else {
      newRoles = [...currentRoles];
      newRoles.splice(roleIndex, 1);
    }
    console.debug("Roles", newRoles);
    this.userForm.get('roles')?.setValue(newRoles);
  }

  cancel(): void {
    this.router.navigate(['/', 'users']);
  }

  isRoleSelected(role: string) {
    const currentRoles = this.userForm.get('roles')?.value || [];
    return currentRoles.indexOf(role) != -1;
  }


  save() {
    console.log("Save call!")
    if (this.userForm.invalid) return;
    const { name, email, roles } = this.userForm.value;
    if (!name || !email || !roles) return;

    if (this.userId) {
      this.usersService.update(this.userId, { name, email, roles })
        .subscribe(user => this.router.navigate(['/', 'users']));
    } else {
      this.usersService.create({ name, email, roles })
        .subscribe(user => this.router.navigate(['/', 'users']));
    }
  }
}
