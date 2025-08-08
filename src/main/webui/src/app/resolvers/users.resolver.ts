import { inject } from '@angular/core';
import { RedirectCommand, ResolveFn, Router } from '@angular/router';
import { User, UsersService } from '../services/users.service';

export const usersResolver: ResolveFn<User[]> = (route, state) => {
    return inject(UsersService).search();
};



export const userResolver: ResolveFn<User> = (route, state) => {
    const userId = route.paramMap.get('userId');
    console.log('userResolver: Resolving user with id:', userId);
    if (!userId) {
        return new RedirectCommand(inject(Router).parseUrl('/'));
    }
    return inject(UsersService).findById(Number(userId));
};
