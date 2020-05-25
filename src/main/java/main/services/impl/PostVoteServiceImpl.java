package main.services.impl;

import main.repositories.PostVoteRepository;
import main.services.PostVoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostVoteServiceImpl implements PostVoteService {
    private PostVoteRepository postVoteRepository;

    @Autowired
    public PostVoteServiceImpl(PostVoteRepository postVoteRepository) {
        this.postVoteRepository = postVoteRepository;
    }

    @Override
    public int getCountLikesByPostId(int postId) {
        return postVoteRepository.getCountLikesByPostId(postId);
    }

    @Override
    public int getCountDislikesByPostId(int postId) {
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
    public int getTotalCountLikesByUserId(int userId) {
        return postVoteRepository.getTotalCountLikesByUserId(userId);
    }

    @Override
    public int getTotalCountDislikesByUserId(int userId) {
        return postVoteRepository.getTotalCountDislikesByUserId(userId);
    }

    @Override
    public boolean userLikeAlreadyExists(int userId, int postId) {
        return postVoteRepository.userLikeAlreadyExists(userId, postId) != null;
    }

    @Override
    public boolean userDislikeAlreadyExists(int userId, int postId) {
        return postVoteRepository.userDislikeAlreadyExists(userId, postId) != null;
    }

    @Override
    public int getIdByUserIdAndPostId(int userId, int postId) {
        return postVoteRepository.getPostVoteIdByUserIdAndPostId(userId, postId);
    }

    @Override
    public void deleteById(int postVoteId) {
        postVoteRepository.deleteById((long) postVoteId);
    }
}
