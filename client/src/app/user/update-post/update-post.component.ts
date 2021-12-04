import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Post} from '../../models/Post';
import {PostService} from '../../service/post.service';
import {ImageUploadService} from '../../service/image-upload.service';
import {NotificationService} from '../../service/notification.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-add-post',
  templateUrl: './update-post.component.html',
  styleUrls: ['./update-post.component.css']
})
export class UpdatePostComponent implements OnInit {

  postForm: FormGroup;
  selectedFile: File;
  isPostCreated = false;
  updatedPost: Post;
  post: Post;
  previewImgURL: any;

  constructor(private postService: PostService,
              private imageUploadService: ImageUploadService,
              private notificationService: NotificationService,
              private router: Router,
              private imageService: ImageUploadService,
              private changeDetect: ChangeDetectorRef,
              private fb: FormBuilder) {
  }

  ngOnInit(): void {
    const id = parseInt(document.location.href.substring(29));
    // const id = 1;
    this.postService.getPost(id)
      .subscribe(data => {
        this.post = data;
        this.imageService.getImageToPost(id)
          .subscribe(data => {
            this.post.image = data;
          });
        this.postForm = this.createPostForm();
      });
  }

  createPostForm(): FormGroup {
    return this.fb.group({
      title: [this.post.title, Validators.compose([Validators.required])],
      caption: [this.post.caption, Validators.compose([Validators.required])],
      category: [this.post.category, Validators.compose([Validators.required])],
    });
  }

  submit(): void {
    this.post.title = this.postForm.value.title;
    this.post.caption = this.postForm.value.caption;
    this.post.category = this.postForm.value.category;
    this.postService.updatePost(this.post)
      .subscribe(data => {
        this.updatedPost = data;

        if (this.selectedFile != null) {
          this.imageUploadService.uploadImageToPost(this.selectedFile, this.updatedPost.id)
            .subscribe(() => {
              this.isPostCreated = true;
              this.router.navigate(['/posts/' + this.updatedPost.userId]);
            });
        } else {
          this.notificationService.showSnackBar('Post updated successfully');
          this.router.navigate(['/posts/' + this.updatedPost.userId]);
        }
        this.notificationService.showSnackBar('Post updated successfully');
      });
  }

  onFileSelected(event): void {
    this.selectedFile = event.target.files[0];

    const reader = new FileReader();
    reader.readAsDataURL(this.selectedFile);
    reader.onload = () => {
      this.previewImgURL = reader.result;
    };
  };
}

