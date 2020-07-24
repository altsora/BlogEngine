package main.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class UserLoginDTO {
    private final long id;
    private final String name;
    private final String photo;
    private final String email;
    private final boolean moderation;
    private final int moderationCount;
    private final boolean settings;
}
