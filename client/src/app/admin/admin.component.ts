import { Component } from '@angular/core';
import {User} from 'src/app/models/User';
import {UserService} from '../service/user.service';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent{

  users: User[] = this.userService.getAllUsers().subscribe(data => this.users = data);
  displayedColumns: string[] = ['id', 'username', 'email'];

  constructor(private userService: UserService) { }
}
