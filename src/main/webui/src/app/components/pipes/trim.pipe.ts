import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
    name: "trim"
})
export class TrimPipe implements PipeTransform {
    transform(value: string, ...args: any[]) {
        const maxSize = args[0] || 250;
        if (value.length > maxSize) {
            return value.substring(0, maxSize) + '...';
        } else {
            return value
        }
        
    }
}