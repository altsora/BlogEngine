package main.services;

import main.api.responses.CommentDTO;
import main.model.entity.PostComment;

import java.util.List;

public interface PostCommentService {
    int getCountCommentsByPostId(long postId);

    List<CommentDTO> getCommentsByPostId(long postId);

    List<PostComment> findAllPostCommentByPostId(long postId);

    PostComment add(PostComment postComment);

    PostComment findById(long postCommentId);
}
