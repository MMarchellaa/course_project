import { Component, OnInit } from '@angular/core';
import {User} from '../../models/User';
import {TokenStorageService} from '../../service/token-storage.service';
import {UserService} from '../../service/user.service';
import {Router} from '@angular/router';
import {Post} from '../../models/Post';
import {PostService} from '../../service/post.service';
import {waitForAsync} from '@angular/core/testing';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css']
})
export class NavigationComponent implements OnInit {

  isLoggedIn = false;
  isDataLoaded = false;
  user: User;
  text: String;
  isAdmin: boolean;
  public searchText: string;

  constructor(private tokenService: TokenStorageService,
              private userService: UserService,
              private postService: PostService,
              private tokenStorage: TokenStorageService,
              private router: Router) { }

  ngOnInit(): void {
    this.isLoggedIn = !!this.tokenService.getToken();

    if(this.isLoggedIn) {
      this.userService.getCurrentUser()
        .subscribe(data => {
          this.user = data;
          this.isDataLoaded = true;
          console.log(this.user.role);
          this.isAdmin = "ROLE_ADMIN" === this.user.role;
        })

    }
  }

  logout(): void {
    this.tokenService.logOut();
    this.router.navigate(['/main']);
  }

  search(): void {
    this.router.navigate(['search/' + this.searchText]);
  }
}
