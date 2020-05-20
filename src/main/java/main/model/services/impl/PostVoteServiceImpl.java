package main.model.services.impl;

import main.model.repositories.PostVoteRepository;
import main.model.services.PostVoteService;
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
}
