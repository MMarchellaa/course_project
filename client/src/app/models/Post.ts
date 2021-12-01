import {Comment} from './Comment';

export interface Post {
  id?: number;
  title: string;
  caption: string;
  image?: string[];
  likes?: number;
  usersLiked?: string[];
  comments?: Comment [];
  username?: string;
  category: string;
  userId?: number;
}
