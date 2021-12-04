import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

const USER_API = 'http://localhost:8080/api/user/';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) {
  }

  getUserById(id: number): Observable<any> {
    return this.http.get(USER_API + id);
  }

  getAllUsers(): Observable<any> {
    return this.http.get(USER_API + 'all');
  }

  getCurrentUser(): Observable<any> {
    return this.http.get(USER_API);
  }

  public isEnable(email): Observable<any> {
    return this.http.get(USER_API + 'isenable/' + email);
  }

  unactivateUser(id: number): Observable<any> {
    return this.http.post(USER_API + 'unactivate', id);
  }

  activateUser(id: number): Observable<any> {
    return this.http.post(USER_API + 'activate', id);
  }
}
