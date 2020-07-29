package main.service.impl;

import lombok.RequiredArgsConstructor;
import main.model.entity.PostComment;
import main.repository.PostCommentRepository;
import main.response.CommentDTO;
import main.response.UserWithPhotoDTO;
import main.service.PostCommentService;
import main.util.TimeUtil;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.ArrayList;
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

    @Override
    public List<CommentDTO> getCommentsByPostId(long postId) {
        List<PostComment> postCommentListRep = findAllPostCommentByPostId(postId);
        List<CommentDTO> commentDTOList = new ArrayList<>();

        for (PostComment postCommentRep : postCommentListRep) {
            long userId = postCommentRep.getUser().getId();
            String userName = postCommentRep.getUser().getName();
            String userPhoto = postCommentRep.getUser().getPhoto();
            UserWithPhotoDTO userWithPhoto = new UserWithPhotoDTO(userId, userName, userPhoto);
            long timestamp = postCommentRep.getTime().toInstant(ZoneOffset.UTC).getEpochSecond();

            CommentDTO commentDTO = CommentDTO.builder()
                    .id(postCommentRep.getId())
                    .timestamp(timestamp)
                    .text(postCommentRep.getText())
                    .user(userWithPhoto)
                    .build();

            commentDTOList.add(commentDTO);
        }
        return commentDTOList;
    }
}
