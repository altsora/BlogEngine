package main.services;

public interface PostVoteService {
    int getCountLikesByPostId(int postId);
    int getCountDislikesByPostId(int postId);
    int getTotalCountLikes();
    int getTotalCountDislikes();
    int getTotalCountLikesByUserId(int userId);
    int getTotalCountDislikesByUserId(int userId);
}
