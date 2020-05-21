package main.services;

import main.model.entities.PostComment;

import java.util.List;

public interface PostCommentService {

    List<PostComment> findAllPostCommentByPostId(int postId);

    int getCountCommentsByPostId(int postId);
}
