package main.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class PostPublicDTO {
    private final long id;
    private final String time;
    private final UserSimpleDTO user;
    private final String title;
    private final String announce;
    private final int likeCount;
    private final int dislikeCount;
    private final int viewCount;
    private final int commentCount;
}
