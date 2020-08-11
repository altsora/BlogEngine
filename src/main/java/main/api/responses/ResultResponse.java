package main.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultResponse extends AbstractResponse{
    private ErrorResponse errors;
    private Long id;
    private String image;
    private String message;
    private Boolean result;
    private String secret;
    private List<TagResponse> tags;
    private UserLoginResponse user;

    public ResultResponse(boolean result) {
        this.result = result;
    }

    public ResultResponse(String message) {
        this.result = false;
        this.message = message;
    }

    public ResultResponse(ErrorResponse errors) {
        this.result = false;
        this.errors = errors;
    }

    public ResultResponse(long id) {
        this.id = id;
    }

    public ResultResponse(List<TagResponse> tags) {
        this.tags = tags;
    }
}
