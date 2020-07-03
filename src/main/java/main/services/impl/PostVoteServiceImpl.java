package main.services.impl;

import lombok.RequiredArgsConstructor;
import main.model.entities.Post;
import main.model.entities.PostVote;
import main.model.entities.User;
import main.model.enums.Rating;
import main.repositories.PostVoteRepository;
import main.services.PostService;
import main.services.PostVoteService;
import main.services.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class PostVoteServiceImpl implements PostVoteService {
    private final PostService postService;
    private final PostVoteRepository postVoteRepository;
    private final UserService userService;

    //=============================================================================

    @Override
    public int getCountLikesByPostId(long postId) {
//        return postVoteRepository.getCountLikesByPostId(postId);
        return postVoteRepository.getCountRatingByPostId(postId, Rating.LIKE);
    }

    @Override
    public int getCountDislikesByPostId(long postId) {
//        return postVoteRepository.getCountDislikesByPostId(postId);
        return postVoteRepository.getCountRatingByPostId(postId, Rating.DISLIKE);
    }

    @Override
    public int getTotalCountLikes() {
//        return postVoteRepository.getTotalCountLikes();
        return postVoteRepository.getTotalCountRating(Rating.LIKE);
    }

    @Override
    public int getTotalCountDislikes() {
//        return postVoteRepository.getTotalCountDislikes();
        return postVoteRepository.getTotalCountRating(Rating.DISLIKE);
    }

    @Override
    public int getTotalCountLikesByUserId(long userId) {
//        return postVoteRepository.getTotalCountLikesByUserId(userId);
        return postVoteRepository.getTotalCountRatingByUserId(userId, Rating.LIKE);
    }

    @Override
    public int getTotalCountDislikesByUserId(long userId) {
//        return postVoteRepository.getTotalCountDislikesByUserId(userId);
        return postVoteRepository.getTotalCountRatingByUserId(userId, Rating.DISLIKE);
    }

    @Override
    public boolean userLikeAlreadyExists(long userId, long postId) {
//        return postVoteRepository.userLikeAlreadyExists(userId, postId) != null;
        return postVoteRepository.ratingUserAlreadyExists(userId, postId, Rating.LIKE) != null;
    }

    @Override
    public boolean userDislikeAlreadyExists(long userId, long postId) {
//        return postVoteRepository.userDislikeAlreadyExists(userId, postId) != null;
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

//    @Override
//    public void replaceLikeWithDislike(long postVoteId) {
//        PostVote postVote = postVoteRepository.findById(postVoteId).orElseThrow();
//        postVote.setValue(Rating.DISLIKE);
//        postVote.setTime(LocalDateTime.now());
//        postVoteRepository.saveAndFlush(postVote);
//    }
//
//    @Override
//    public void replaceDislikeWithLike(long postVoteId) {
//        PostVote postVote = postVoteRepository.findById(postVoteId).orElseThrow();
//        postVote.setValue(Rating.LIKE);
//        postVote.setTime(LocalDateTime.now());
//        postVoteRepository.saveAndFlush(postVote);
//    }

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
