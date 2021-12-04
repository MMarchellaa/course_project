import {Component} from '@angular/core';
import {User} from 'src/app/models/User';
import {UserService} from '../service/user.service';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent {

  users: User[] = this.userService.getAllUsers().subscribe(data => this.users = data);
  displayedColumns: string[] = ['id', 'username', 'email', 'status', 'activate-button', 'deactivate-button'];

  constructor(private userService: UserService) {
  }

  activate(id: number): void {
    this.userService.activateUser(id)
      .subscribe(data => {
        window.location.reload();
      });
  }

  unactivate(id: number): void {
    this.userService.unactivateUser(id)
      .subscribe(data => {
        window.location.reload();
      });
  }
}
