package main.api.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
public class PublicPostsDTO {
    private final int count;
    @Singular private final List<PostPublicDTO> posts;
}
