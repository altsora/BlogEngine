package main.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class CommentDTO {
    private final long id;
    private final String time;
    private final String text;
    private final UserWithPhotoDTO user;
}
