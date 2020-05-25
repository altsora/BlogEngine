package main.services;

public interface PostVoteService {
    int getCountLikesByPostId(int postId);
    int getCountDislikesByPostId(int postId);
    int getTotalCountLikes();
    int getTotalCountDislikes();
    int getTotalCountLikesByUserId(int userId);
    int getTotalCountDislikesByUserId(int userId);
    boolean userLikeAlreadyExists(int userId, int postId);
    boolean userDislikeAlreadyExists(int userId, int postId);
    int getIdByUserIdAndPostId(int userId, int postId);
    void deleteById(int postVoteId);
}
