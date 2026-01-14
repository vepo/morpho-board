import { ApplicationRef, ComponentFactoryResolver, ComponentRef, EmbeddedViewRef, inject, Injectable, Injector, TemplateRef } from '@angular/core';
import { ToastComponent } from './toast.component';

export interface ToastOptions {
    duration?: number;
    type?: 'success' | 'error' | 'warning' | 'info';
    position?: 'top-right' | 'top-left' | 'bottom-right' | 'bottom-left' | 'top-center' | 'bottom-center';
    closeable?: boolean;
    template?: TemplateRef<any>;
    context?: any;
}

@Injectable({
    providedIn: 'root'
})
export class ToastService {
    private toasts: ComponentRef<ToastComponent>[] = [];
    private containerElement?: HTMLElement;

    private componentFactoryResolver: ComponentFactoryResolver = inject(ComponentFactoryResolver);
    private appRef: ApplicationRef = inject(ApplicationRef);
    private injector: Injector = inject(Injector);
    constructor() {
        this.createContainer();
    }

    success(message: string, duration?: number): void {
        this.show(message, {
            type: 'success',
            duration: duration || 3000
        });
    }

    error(message: string, duration?: number): void {
        this.show(message, {
            type: 'error',
            duration: duration || 5000
        });
    }

    warning(message: string, duration?: number): void {
        this.show(message, {
            type: 'warning',
            duration: duration || 4000
        });
    }

    info(message: string, duration?: number): void {
        this.show(message, {
            type: 'info',
            duration: duration || 3000
        });
    }

    show(message: string, options?: ToastOptions): void {
        const toastOptions: ToastOptions = {
            duration: 3000,
            type: 'info',
            position: 'top-right',
            closeable: true,
            ...options
        };

        const componentRef = this.createToastComponent(message, toastOptions);
        this.toasts.push(componentRef);

        if (toastOptions.duration && toastOptions.duration > 0) {
            // setTimeout(() => {
            //     this.removeToast(componentRef);
            // }, toastOptions.duration);
        }
    }

    showTemplate(template: TemplateRef<any>, options?: ToastOptions, context?: any): void {
        const toastOptions: ToastOptions = {
            duration: 3000,
            type: 'info',
            position: 'top-right',
            closeable: true,
            template,
            context,
            ...options
        };

        const componentRef = this.createToastComponent('', toastOptions);
        this.toasts.push(componentRef);

        if (toastOptions.duration && toastOptions.duration > 0) {
            // setTimeout(() => {
            //     this.removeToast(componentRef);
            // }, toastOptions.duration);
        }
    }

    clearAll(): void {
        this.toasts.forEach(toast => {
            this.appRef.detachView(toast.hostView);
            toast.destroy();
        });
        this.toasts = [];
    }

    private createContainer(): void {
        this.containerElement = document.createElement('div');
        this.containerElement.className = 'toast-container';
        document.body.appendChild(this.containerElement);
    }

    private createToastComponent(message: string, options: ToastOptions): ComponentRef<ToastComponent> {
        const componentFactory = this.componentFactoryResolver.resolveComponentFactory(ToastComponent);
        const componentRef = componentFactory.create(this.injector);

        // Set component inputs
        componentRef.instance.message = message;
        componentRef.instance.type = options.type!;
        componentRef.instance.position = options.position!;
        componentRef.instance.closeable = options.closeable!;
        componentRef.instance.template = options.template;
        componentRef.instance.context = options.context;

        // Handle close event
        componentRef.instance.close.subscribe(() => {
            this.removeToast(componentRef);
        });

        // Attach to application
        this.appRef.attachView(componentRef.hostView);

        // Get DOM element
        const domElem = (componentRef.hostView as EmbeddedViewRef<any>).rootNodes[0] as HTMLElement;

        // Add position class
        domElem.classList.add(`toast-${options.position}`);

        // Append to container
        this.containerElement?.appendChild(domElem);

        return componentRef;
    }

    private removeToast(toastRef: ComponentRef<ToastComponent>): void {
        if (!toastRef || !toastRef.instance) return;

        const index = this.toasts.indexOf(toastRef);
        if (index > -1) {
            this.toasts.splice(index, 1);
        }

        // Add fade-out animation
        toastRef.instance.isVisible = false;

        setTimeout(() => {
            this.appRef.detachView(toastRef.hostView);
            toastRef.destroy();
        }, 300); // Match this with CSS transition duration
    }
}