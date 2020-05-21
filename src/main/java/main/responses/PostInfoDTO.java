package main.responses;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostInfoDTO extends PostSimpleDTO {
    private int likeCount;
    private int dislikeCount;
    private int viewCount;
    private int commentCount;

    public PostInfoDTO(PostSimpleDTO postSimpleDTO) {
        super(postSimpleDTO);
    }

    public PostInfoDTO(PostSimpleDTO postSimpleDTO, PostInfoDTO postInfoDTO) {
        super(postSimpleDTO);
        this.likeCount = postInfoDTO.likeCount;
        this.dislikeCount = postInfoDTO.dislikeCount;
        this.viewCount = postInfoDTO.viewCount;
        this.commentCount = postInfoDTO.commentCount;
    }

    public void increaseLikeCount() {
        likeCount++;
    }

    public void increaseDislikeCount() {
        dislikeCount++;
    }
}
