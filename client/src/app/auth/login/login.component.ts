import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../service/auth.service';
import {TokenStorageService} from '../../service/token-storage.service';
import {Router} from '@angular/router';
import {NotificationService} from '../../service/notification.service';
import {UserService} from '../../service/user.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})

export class LoginComponent implements OnInit {

  public loginForm: FormGroup;

  constructor(
    private authService: AuthService,
    private tokenStorage: TokenStorageService,
    private notificationService: NotificationService,
    private router: Router,
    private fb: FormBuilder,
    private changeDetect: ChangeDetectorRef,
    private userService: UserService) {
    if (this.tokenStorage.getUser()) {
      this.router.navigate(['main']);
    }
  }

  ngOnInit(): void {
    this.loginForm = this.createLoginForm();
  }

  createLoginForm(): FormGroup {
    return this.fb.group({
      username: ['', Validators.compose([Validators.required, Validators.email])],
      password: ['', Validators.compose([Validators.required])],
    });
  }

  submit(): void {
    let userData;
    let isEnabled;
    this.authService.login({
      username: this.loginForm.value.username,
      password: this.loginForm.value.password
    }).subscribe(data => {
      userData = data;
      this.userService.isEnable(this.loginForm.value.username)
        .subscribe(data => {
          isEnabled = data;
          if (isEnabled) {
            this.tokenStorage.saveToken(userData.token);
            this.tokenStorage.saveUser(userData);
            this.notificationService.showSnackBar('Successfully logged in');
            this.router.navigate(['/']);
            window.location.reload();
          }
        });
    }, error => {
      console.log(error);
      this.notificationService.showSnackBar(error.message);
    });
  }
}
