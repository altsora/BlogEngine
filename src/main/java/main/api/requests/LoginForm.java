package main.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class LoginForm {
    @JsonProperty("e_mail") private String email;
    @JsonProperty("password") private String password;
}
