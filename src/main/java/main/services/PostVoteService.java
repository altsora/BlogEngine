package main.services;

public interface PostVoteService {

    int getCountLikesByPostId(int postId);
    int getCountDislikesByPostId(int postId);
}
