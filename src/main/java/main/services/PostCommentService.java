package main.services;

import main.model.entities.PostComment;

import java.util.List;

public interface PostCommentService {
    List<PostComment> findAllPostCommentByPostId(long postId);

    int getCountCommentsByPostId(long postId);
}
