import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
    name: "normalize"
})
export class NormalizePipe implements PipeTransform {
    transform(value: string, ...args: any[]) {
        return value.split('_').map(i => i.substring(0, 1).toUpperCase() + i.substring(1).toLowerCase()).join(' ')
    }
}