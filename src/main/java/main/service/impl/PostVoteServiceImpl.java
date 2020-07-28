package main.service.impl;

import main.model.entity.Post;
import main.model.entity.PostVote;
import main.model.entity.User;
import main.model.enums.Rating;
import main.repository.PostVoteRepository;
import main.service.PostService;
import main.service.PostVoteService;
import main.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class PostVoteServiceImpl implements PostVoteService {
    private final PostService postService;
    private final PostVoteRepository postVoteRepository;
    private final UserService userService;

    public PostVoteServiceImpl(@Lazy PostService postService,
                               @Lazy PostVoteRepository postVoteRepository,
                               @Lazy UserService userService) {
        this.postService = postService;
        this.postVoteRepository = postVoteRepository;
        this.userService = userService;
    }

    //=============================================================================

    @Override
    public int getCountLikesByPostId(long postId) {
        return postVoteRepository.getCountRatingByPostId(postId, Rating.LIKE);
    }

    @Override
    public int getCountDislikesByPostId(long postId) {
        return postVoteRepository.getCountRatingByPostId(postId, Rating.DISLIKE);
    }

    @Override
    public int getTotalCountLikes() {
        return postVoteRepository.getTotalCountRating(Rating.LIKE);
    }

    @Override
    public int getTotalCountDislikes() {
        return postVoteRepository.getTotalCountRating(Rating.DISLIKE);
    }

    @Override
    public int getTotalCountLikesByUserId(long userId) {
        return postVoteRepository.getTotalCountRatingByUserId(userId, Rating.LIKE);
    }

    @Override
    public int getTotalCountDislikesByUserId(long userId) {
        return postVoteRepository.getTotalCountRatingByUserId(userId, Rating.DISLIKE);
    }

    @Override
    public boolean userLikeAlreadyExists(long userId, long postId) {
        return postVoteRepository.ratingUserAlreadyExists(userId, postId, Rating.LIKE) != null;
    }

    @Override
    public boolean userDislikeAlreadyExists(long userId, long postId) {
        return postVoteRepository.ratingUserAlreadyExists(userId, postId, Rating.DISLIKE) != null;
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
        postVote.setTime(LocalDateTime.now(ZoneId.of("UTC")));
        postVote.setValue(value);
        postVoteRepository.saveAndFlush(postVote);
    }

    @Override
    public void replaceValue(long postVoteId) {
        PostVote postVote = postVoteRepository.findById(postVoteId).orElseThrow();
        Rating value = postVote.getValue() == Rating.LIKE ?
                Rating.DISLIKE : Rating.LIKE;
        postVote.setValue(value);
        postVote.setTime(LocalDateTime.now(ZoneId.of("UTC")));
        postVoteRepository.saveAndFlush(postVote);
    }
}