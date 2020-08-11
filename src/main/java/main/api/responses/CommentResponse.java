package main.api.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class CommentResponse {
    private final long id;
    private final long timestamp;
    private final String text;
    private final UserWithPhotoResponse user;
}
