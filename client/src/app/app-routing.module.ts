import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {LoginComponent} from './auth/login/login.component';
import {RegisterComponent} from './auth/register/register.component';
import {IndexComponent} from './layout/index/index.component';
import {AuthGuardService} from './helper/auth-guard.service';
import {SearchComponent} from './search/search.component';
import {UserPostsComponent} from './user/user-posts/user-posts.component';
import {AddPostComponent} from './user/add-post/add-post.component';
import {AdminComponent} from './admin/admin.component';
import {UpdatePostComponent} from './user/update-post/update-post.component';

const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {
    path: 'search', pathMatch: 'prefix', component: SearchComponent,
    children: [{
      path: '**',
      component: SearchComponent
    }]
  },
  {path: 'admin', component: AdminComponent},
  {path: 'update', component: UpdatePostComponent},
  {path: 'main', component: IndexComponent},
  {
    path: 'posts', pathMatch: 'prefix', component: UserPostsComponent,
    children: [{
      path: '**',
      component: UserPostsComponent
    }]
  },
  {path: 'add', component: AddPostComponent, canActivate: [AuthGuardService]},
  {path: '', redirectTo: 'main', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
