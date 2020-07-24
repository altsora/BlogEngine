package main.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PasswordChangeForm {
    @JsonProperty("code") private String code;
    @JsonProperty("password") private String password;
    @JsonProperty("captcha") private String captcha;
    @JsonProperty("captcha_secret") private String captchaSecret;
}
