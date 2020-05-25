package main.services;

import main.model.entities.PostVote;

public interface PostVoteService {
    int getCountLikesByPostId(long postId);
    int getCountDislikesByPostId(long postId);
    int getIdByUserIdAndPostId(long userId, long postId);
    int getTotalCountDislikes();
    int getTotalCountDislikesByUserId(long userId);
    int getTotalCountLikes();
    int getTotalCountLikesByUserId(long userId);

    boolean userDislikeAlreadyExists(long userId, long postId);
    boolean userLikeAlreadyExists(long userId, long postId);

    void addPostVote(PostVote postVote);
    void deleteById(long postVoteId);
    void replaceDislikeWithLike(long postVoteId);
    void replaceLikeWithDislike(long postVoteId);

    PostVote findById(long postVoteId);
}
