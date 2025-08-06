import { Directive, inject, Input, TemplateRef, ViewContainerRef } from '@angular/core';
import { AuthService } from '../services/auth.service';

@Directive({
    selector: '[role]'
})
export class RoleDirective {
    private hasView = false;
    private templateRef = inject(TemplateRef);
    private viewContainerRef = inject(ViewContainerRef);

    private authService = inject(AuthService);

    @Input() set role(role: string) {
        console.debug("Checking role...", role);
        const hasRole = this.authService.hasRole(role);
        if (hasRole && !this.hasView) {
            this.viewContainerRef.createEmbeddedView(this.templateRef);
            this.hasView = true;
        } else if (!hasRole && this.hasView) {
            this.viewContainerRef.clear();
            this.hasView = false;
        }
    }
}