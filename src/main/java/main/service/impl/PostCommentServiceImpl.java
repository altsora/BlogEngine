package main.service.impl;

import lombok.RequiredArgsConstructor;
import main.model.entity.PostComment;
import main.repository.PostCommentRepository;
import main.service.PostCommentService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostCommentServiceImpl implements PostCommentService {
    private final PostCommentRepository postCommentRepository;

    //==================================================================================================================

    @Override
    public List<PostComment> findAllPostCommentByPostId(long postId) {
        return postCommentRepository.findAllPostCommentByPostId(postId, Sort.by(Sort.Direction.ASC, PostCommentRepository.COMMENT_TIME));
    }

    @Override
    public PostComment findById(long postCommentId) {
        return postCommentRepository.findById(postCommentId).orElse(null);
    }

    @Override
    public PostComment add(PostComment postComment) {
        return postCommentRepository.saveAndFlush(postComment);
    }

    @Override
    public int getCountCommentsByPostId(long postId) {
        return postCommentRepository.getCountCommentsByPostId(postId);
    }
}
