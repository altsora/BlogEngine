package main.model.services;

public interface PostVoteService {

    int getCountLikesByPostId(int postId);
    int getCountDislikesByPostId(int postId);
}
