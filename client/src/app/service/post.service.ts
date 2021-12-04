import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Post} from '../models/Post';
import {Observable} from 'rxjs';

const POST_API = 'http://localhost:8080/api/post/';

@Injectable({
  providedIn: 'root'
})
export class PostService {

  constructor(private http: HttpClient) {
  }

  createPost(post: Post): Observable<any> {
    return this.http.post(POST_API + 'create', post);
  }

  updatePost(post: Post): Observable<any> {
    return this.http.post(POST_API + 'user/posts/update', post);
  }

  getAllPosts(): Observable<any> {
    return this.http.get(POST_API + 'all');
  }

  getPosts(id: number): Observable<any> {
    return this.http.get(POST_API + 'user/posts/' + id);
  }

  getPost(id: number): Observable<any> {
    return this.http.get(POST_API + id);
  }

  deletePost(id: number): Observable<any> {
    return this.http.post(POST_API + id + '/delete', null);
  }

  likePost(id: number, username: string): Observable<any> {
    return this.http.post(POST_API + id + '/' + username + '/like', null);
  }

  search(text: string): Observable<any> {
    return this.http.get(POST_API + 'search/' + text);
  }
}
