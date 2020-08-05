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
public class ResultDTO {
    private ErrorsDTO errors;
    private Long id;
    private String image;
    private String message;
    private Boolean result;
    private String secret;
    private List<TagDTO> tags;
    private UserLoginDTO user;

    public ResultDTO(boolean result) {
        this.result = result;
    }

    public ResultDTO(String message) {
        this.result = false;
        this.message = message;
    }

    public ResultDTO(ErrorsDTO errors) {
        this.result = false;
        this.errors = errors;
    }

    public ResultDTO(long id) {
        this.id = id;
    }

    public ResultDTO(List<TagDTO> tags) {
        this.tags = tags;
    }
}
