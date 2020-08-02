package main.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorsDTO {
    private String captcha;
    private String email;
    private String code;
    private String image;
    private String name;
    private String password;
    private String photo;
    private String text;
    private String title;
}
