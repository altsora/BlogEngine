package main.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class BlogDTO {
    private final String title;
    private final String subtitle;
    private final String phone;
    private final String email;
    private final String copyright;
    private final String copyrightFrom;
}
