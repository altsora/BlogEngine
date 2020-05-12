package main.model.responses;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostVoteDTO extends PostSimpleDTO {
    private int likeCount;
    private int dislikeCount;
    private int viewCount;
    private int commentCount;

    public void increaseLikeCount() {
        likeCount++;
    }

    public void increaseDislikeCount() {
        dislikeCount++;
    }

    public void increaseCommentCount() {
        commentCount++;
    }
}
