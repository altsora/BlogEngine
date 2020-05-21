package main.responses;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BlogDTO {
    private String title;
    private String subtitle;
    private String phone;
    private String email;
    private String copyright;
    private String copyrightFrom;
}
