package main.api.responses;


import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
public class PostFullResponse extends AbstractResponse {
    private final long id;
    private final long timestamp;
    private final boolean active;
    private final UserSimpleResponse user;
    private final String title;
    private final String text;
    private final int likeCount;
    private final int dislikeCount;
    private final int viewCount;
    private final int commentCount;
    @Singular
    private final List<CommentResponse> comments;
    @Singular
    private final List<String> tags;
}
