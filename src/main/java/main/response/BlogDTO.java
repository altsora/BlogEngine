package main.response;

import lombok.ToString;
import lombok.RequiredArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "blog")
@ConstructorBinding
@RequiredArgsConstructor
@ToString
@Getter
public class BlogDTO {
    private final String title;
    private final String subtitle;
    private final String phone;
    private final String email;
    private final String copyright;
    private final String copyrightFrom;
}
