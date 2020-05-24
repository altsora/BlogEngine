package main.responses;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class LoginForm {
    @NotBlank
    @Size(max = 60)
    private String e_mail;

    @NotBlank
    @Size(max = 40)
    private String password;
}
