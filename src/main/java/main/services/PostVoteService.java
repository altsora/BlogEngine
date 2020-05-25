package main.services;

public interface PostVoteService {
    int getCountLikesByPostId(long postId);
    int getCountDislikesByPostId(long postId);
    int getTotalCountLikes();
    int getTotalCountDislikes();
    int getTotalCountLikesByUserId(long userId);
    int getTotalCountDislikesByUserId(long userId);
    boolean userLikeAlreadyExists(long userId, long postId);
    boolean userDislikeAlreadyExists(long userId, long postId);
    int getIdByUserIdAndPostId(long userId, long postId);
    void deleteById(long postVoteId);
}
