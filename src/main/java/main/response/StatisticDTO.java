package main.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class StatisticDTO {
    private final int dislikesCount;
    private final long firstPublication;
    private final int likesCount;
    private final int postsCount;
    private final int viewsCount;
}
