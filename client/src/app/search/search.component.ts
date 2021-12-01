import { Component, OnInit } from '@angular/core';
import {Post} from 'src/app/models/Post';
import {User} from 'src/app/models/User';
import {PostService} from 'src/app/service/post.service';
import {UserService} from 'src/app/service/user.service';
import {CommentService} from 'src/app/service/comment.service';
import {NotificationService} from 'src/app/service/notification.service';
import {ImageUploadService} from 'src/app/service/image-upload.service';
import {TokenStorageService} from 'src/app/service/token-storage.service';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {

  isLoggedIn = false;
  isPostsLoaded = false;
  posts: Post[];
  isUserDataLoaded = false;
  user: User;

  constructor(private postService: PostService,
    private userService: UserService,
    private commentService: CommentService,
    private notificationService: NotificationService,
    private imageService: ImageUploadService,
    private tokenService: TokenStorageService
  ) { }

  ngOnInit(): void {
    const text = document.location.href.substring(29);
    console.log(text)
    this.postService.search(text)
      .subscribe(data => {
        console.log(data);
        this.posts = data;
        this.getImagesToPosts(this.posts);
        this.getCommentsToPosts(this.posts);
        this.isPostsLoaded = true;
      });
    this.isLoggedIn = !!this.tokenService.getToken();

    if (this.isLoggedIn) {
      this.userService.getCurrentUser()
        .subscribe(data => {
          console.log(data);
          this.user = data;
          this.isUserDataLoaded = true;
        })
    }
  }

  getImagesToPosts(posts: Post[]): void {
    posts.forEach(p => {
      this.imageService.getImageToPost(p.id)
        .subscribe(data => {
          p.image = data;
        })
    });
  }

  getCommentsToPosts(posts: Post[]): void {
    posts.forEach(p => {
      this.commentService.getCommentsToPost(p.id)
        .subscribe(data => {
          p.comments = data
        })
    });
  }
  likePost(postId: number, postIndex: number): void {
    this.isLoggedIn = !!this.tokenService.getToken();

    if (this.isLoggedIn) {
      const post = this.posts[postIndex];
      console.log(post);

      if (!post.usersLiked.includes(this.user.username)) {
        this.postService.likePost(postId, this.user.username)
          .subscribe(() => {
            post.usersLiked.push(this.user.username);
            this.notificationService.showSnackBar('Liked!');
          });
      } else {
        this.postService.likePost(postId, this.user.username)
          .subscribe(() => {
            const index = post.usersLiked.indexOf(this.user.username, 0);
            if (index > -1) {
              post.usersLiked.splice(index, 1);
            }
          });
      }
    }
  }

  postComment(message: string, postId: number, postIndex: number): void {
    const post = this.posts[postIndex];

    console.log(post);
    this.commentService.addToCommentToPost(postId, message)
      .subscribe(data => {
        console.log(data);
        post.comments.push(data);
      });
  }
}
