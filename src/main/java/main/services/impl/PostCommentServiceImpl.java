package main.services.impl;

import main.model.entities.PostComment;
import main.repositories.PostCommentRepository;
import main.services.PostCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostCommentServiceImpl implements PostCommentService {
    private PostCommentRepository postCommentRepository;

    @Autowired
    public PostCommentServiceImpl(PostCommentRepository postCommentRepository) {
        this.postCommentRepository = postCommentRepository;
    }

    @Override
    public List<PostComment> findAllPostCommentByPostId(int postId) {
        return postCommentRepository.findAllPostCommentByPostId(postId, Sort.by(Sort.Direction.ASC, PostCommentRepository.COMMENT_TIME));
    }

    @Override
    public int getCountCommentsByPostId(int postId) {
        return postCommentRepository.getCountCommentsByPostId(postId);
    }
}
