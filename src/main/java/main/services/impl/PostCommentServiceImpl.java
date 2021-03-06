package main.services.impl;

import lombok.RequiredArgsConstructor;
import main.api.responses.CommentResponse;
import main.api.responses.UserWithPhotoResponse;
import main.model.entities.PostComment;
import main.repositories.PostCommentRepository;
import main.services.PostCommentService;
import main.utils.TimeUtil;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostCommentServiceImpl implements PostCommentService {
    private final PostCommentRepository postCommentRepository;

    //==================================================================================================================

    @Override
    public List<PostComment> findAllPostCommentByPostId(long postId) {
        return postCommentRepository
                .findAllPostCommentByPostId(postId, Sort.by(Sort.Direction.ASC, PostCommentRepository.COMMENT_TIME));
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

    @Override
    public List<CommentResponse> getCommentsByPostId(long postId) {
        List<PostComment> postCommentListRep = findAllPostCommentByPostId(postId);
        List<CommentResponse> commentResponseList = new ArrayList<>();

        for (PostComment postCommentRep : postCommentListRep) {
            long userId = postCommentRep.getUser().getId();
            String userName = postCommentRep.getUser().getName();
            String userPhoto = postCommentRep.getUser().getPhoto();
            UserWithPhotoResponse userWithPhoto = new UserWithPhotoResponse(userId, userName, userPhoto);
            long timestamp = TimeUtil.getTimestampFromLocalDateTime(postCommentRep.getTime());

            CommentResponse commentResponse = CommentResponse.builder()
                    .id(postCommentRep.getId())
                    .timestamp(timestamp)
                    .text(postCommentRep.getText())
                    .user(userWithPhoto)
                    .build();

            commentResponseList.add(commentResponse);
        }
        return commentResponseList;
    }
}
