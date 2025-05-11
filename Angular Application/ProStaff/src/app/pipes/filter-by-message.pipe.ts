import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'filterByMessage',
  standalone:false
})
export class FilterByMessagePipe implements PipeTransform {
  transform(notifications: any[], searchTerm: string): any[] {
    if (!notifications || !searchTerm) {
      return notifications;
    }

    const lowerTerm = searchTerm.toLowerCase();
    return notifications.filter(notif =>
      notif.message.toLowerCase().includes(lowerTerm)
    );
  }
}
