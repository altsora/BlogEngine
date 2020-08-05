package main.services.impl;

import main.model.entity.Post;
import main.model.entity.PostVote;
import main.model.entity.User;
import main.model.enums.Rating;
import main.repositories.PostVoteRepository;
import main.services.PostService;
import main.services.PostVoteService;
import main.services.UserService;
import main.utils.TimeUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static main.model.enums.Rating.DISLIKE;
import static main.model.enums.Rating.LIKE;

@Service
public class PostVoteServiceImpl implements PostVoteService {
    private final PostService postService;
    private final PostVoteRepository postVoteRepository;
    private final UserService userService;

    public PostVoteServiceImpl(
            @Lazy PostService postService,
            @Lazy PostVoteRepository postVoteRepository,
            @Lazy UserService userService) {
        this.postService = postService;
        this.postVoteRepository = postVoteRepository;
        this.userService = userService;
    }

    //=============================================================================

    @Override
    public int getCountLikesByPostId(long postId) {
        return postVoteRepository.getCountRatingByPostId(postId, LIKE);
    }

    @Override
    public int getCountDislikesByPostId(long postId) {
        return postVoteRepository.getCountRatingByPostId(postId, DISLIKE);
    }

    @Override
    public int getTotalCountLikes() {
        return postVoteRepository.getTotalCountRating(LIKE);
    }

    @Override
    public int getTotalCountDislikes() {
        return postVoteRepository.getTotalCountRating(DISLIKE);
    }

    @Override
    public int getTotalCountLikesByUserId(long userId) {
        return postVoteRepository.getTotalCountRatingByUserId(userId, LIKE);
    }

    @Override
    public int getTotalCountDislikesByUserId(long userId) {
        return postVoteRepository.getTotalCountRatingByUserId(userId, DISLIKE);
    }

    @Override
    public boolean userLikeAlreadyExists(long userId, long postId) {
        return postVoteRepository.ratingUserAlreadyExists(userId, postId, LIKE) != null;
    }

    @Override
    public boolean userDislikeAlreadyExists(long userId, long postId) {
        return postVoteRepository.ratingUserAlreadyExists(userId, postId, DISLIKE) != null;
    }

    @Override
    public int getIdByUserIdAndPostId(long userId, long postId) {
        return postVoteRepository.getPostVoteIdByUserIdAndPostId(userId, postId);
    }

    @Override
    public void deleteById(long postVoteId) {
        postVoteRepository.deleteById(postVoteId);
    }

    @Override
    public PostVote findById(long postVoteId) {
        return postVoteRepository.findById(postVoteId).orElse(null);
    }

    @Override
    public void setRating(long userId, long postId, Rating value) {
        User user = userService.findById(userId);
        Post post = postService.findById(postId);
        PostVote postVote = new PostVote();
        postVote.setUser(user);
        postVote.setPost(post);
        postVote.setTime(LocalDateTime.now(TimeUtil.TIME_ZONE));
        postVote.setValue(value);
        postVoteRepository.saveAndFlush(postVote);
    }

    @Override
    public void replaceValue(long postVoteId) {
        PostVote postVote = postVoteRepository.findById(postVoteId).orElseThrow();
        Rating value = postVote.getValue() == LIKE ?
                DISLIKE : LIKE;
        postVote.setValue(value);
        postVote.setTime(LocalDateTime.now(TimeUtil.TIME_ZONE));
        postVoteRepository.saveAndFlush(postVote);
    }
}
