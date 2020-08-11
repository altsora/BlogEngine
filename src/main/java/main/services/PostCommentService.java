package main.services;

import main.api.responses.CommentResponse;
import main.model.entities.PostComment;

import java.util.List;

public interface PostCommentService {
    int getCountCommentsByPostId(long postId);

    List<CommentResponse> getCommentsByPostId(long postId);

    List<PostComment> findAllPostCommentByPostId(long postId);

    PostComment add(PostComment postComment);

    PostComment findById(long postCommentId);
}
