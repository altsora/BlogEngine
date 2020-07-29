package main.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class CommentDTO {
    private final long id;
    private final long timestamp;
    private final String text;
    private final UserWithPhotoDTO user;
}
