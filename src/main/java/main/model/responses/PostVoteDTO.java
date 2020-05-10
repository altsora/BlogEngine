package main.model.responses;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostVoteDTO extends PostSimpleDTO {
    private int likeCount;
    private int dislikeCount;
    private int viewCount;

    public PostVoteDTO(
            int id, String time, UserSimple user,
            String title, String announce, int likeCount,
            int dislikeCount, int viewCount
    ) {
        super(id, time, user, title, announce);
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.viewCount = viewCount;
    }

    public void increaseLikeCount() {
        likeCount++;
    }

    public void increaseDislikeCount() {
        dislikeCount++;
    }
}
