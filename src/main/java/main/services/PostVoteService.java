package main.services;

import main.model.entity.PostVote;
import main.model.enums.Rating;

public interface PostVoteService {
    boolean userDislikeAlreadyExists(long userId, long postId);

    boolean userLikeAlreadyExists(long userId, long postId);

    int getCountDislikesByPostId(long postId);

    int getCountLikesByPostId(long postId);

    int getIdByUserIdAndPostId(long userId, long postId);

    int getTotalCountDislikes();

    int getTotalCountDislikesByUserId(long userId);

    int getTotalCountLikes();

    int getTotalCountLikesByUserId(long userId);

    void deleteById(long postVoteId);

    void replaceValue(long postVoteId);

    void setRating(long userId, long postId, Rating value);

    PostVote findById(long postVoteId);
}
