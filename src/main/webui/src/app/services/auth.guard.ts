import { CanActivateFn } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';
import { JwtHelperService } from '@auth0/angular-jwt';

export const authGuard: CanActivateFn = () => {
    const authService = inject(AuthService);
    const router = inject(Router);
    const jwtHelper = inject(JwtHelperService);

    var token = authService.getToken();

    if (!token || jwtHelper.isTokenExpired(token)) {
        if (token) {
            authService.logout();
        }

        router.navigate(['/login']);
        return false;
    }

    return true;
};