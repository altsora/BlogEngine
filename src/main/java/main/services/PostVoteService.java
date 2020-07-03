package main.services;

import main.model.entities.PostVote;
import main.model.enums.Rating;

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

    void deleteById(long postVoteId);

    PostVote findById(long postVoteId);

    void setRating(long userId, long postId, Rating value);

    void replaceValue(long postVoteId);
}
