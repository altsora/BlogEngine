package main.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateProfileForm {
    @JsonProperty("name") private String name;
    @JsonProperty("email") private String email;
    @JsonProperty("password") private String password;
    @JsonProperty("removePhoto") private Integer removePhoto;
    @JsonProperty("photo") private String photo;
}
