import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { User, UsersService } from '../services/users.service';

export const usersResolver: ResolveFn<User[]> = (route, state) => {
    return inject(UsersService).search();
};
