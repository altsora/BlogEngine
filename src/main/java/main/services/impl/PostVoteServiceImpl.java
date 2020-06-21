package main.services.impl;

import lombok.RequiredArgsConstructor;
import main.model.entities.PostVote;
import main.repositories.PostVoteRepository;
import main.services.PostVoteService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostVoteServiceImpl implements PostVoteService {
    private final PostVoteRepository postVoteRepository;

    //=============================================================================

    @Override
    public int getCountLikesByPostId(long postId) {
        return postVoteRepository.getCountLikesByPostId(postId);
    }

    @Override
    public int getCountDislikesByPostId(long postId) {
        return postVoteRepository.getCountDislikesByPostId(postId);
    }

    @Override
    public int getTotalCountLikes() {
        return postVoteRepository.getTotalCountLikes();
    }

    @Override
    public int getTotalCountDislikes() {
        return postVoteRepository.getTotalCountDislikes();
    }

    @Override
    public int getTotalCountLikesByUserId(long userId) {
        return postVoteRepository.getTotalCountLikesByUserId(userId);
    }

    @Override
    public int getTotalCountDislikesByUserId(long userId) {
        return postVoteRepository.getTotalCountDislikesByUserId(userId);
    }

    @Override
    public boolean userLikeAlreadyExists(long userId, long postId) {
        return postVoteRepository.userLikeAlreadyExists(userId, postId) != null;
    }

    @Override
    public boolean userDislikeAlreadyExists(long userId, long postId) {
        return postVoteRepository.userDislikeAlreadyExists(userId, postId) != null;
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
    public void replaceLikeWithDislike(long postVoteId) {
        PostVote postVote = postVoteRepository.findById(postVoteId).orElseThrow();
        postVote.setValue((byte) -1);
        postVote.setTime(LocalDateTime.now());
        postVoteRepository.saveAndFlush(postVote);
    }

    @Override
    public void replaceDislikeWithLike(long postVoteId) {
        PostVote postVote = postVoteRepository.findById(postVoteId).orElseThrow();
        postVote.setValue((byte) 1);
        postVote.setTime(LocalDateTime.now());
        postVoteRepository.saveAndFlush(postVote);
    }

    @Override
    public void addPostVote(PostVote postVote) {
        postVoteRepository.saveAndFlush(postVote);
    }

    @Override
    public PostVote findById(long postVoteId) {
        return postVoteRepository.findById(postVoteId).orElse(null);
    }
}
