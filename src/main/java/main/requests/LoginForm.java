package main.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
public class LoginForm {
    @JsonProperty("e_mail") private String email;
    @JsonProperty("password") private String password;
}
