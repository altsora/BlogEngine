package main.service;

import main.model.entity.PostComment;

import java.util.List;

public interface PostCommentService {
    List<PostComment> findAllPostCommentByPostId(long postId);

    PostComment findById(long postCommentId);

    PostComment add(PostComment postComment);

    int getCountCommentsByPostId(long postId);
}
