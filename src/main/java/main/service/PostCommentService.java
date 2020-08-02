package main.service;

import main.model.entity.PostComment;
import main.response.CommentDTO;

import java.util.List;

public interface PostCommentService {
    int getCountCommentsByPostId(long postId);

    List<CommentDTO> getCommentsByPostId(long postId);

    List<PostComment> findAllPostCommentByPostId(long postId);

    PostComment add(PostComment postComment);

    PostComment findById(long postCommentId);
}
