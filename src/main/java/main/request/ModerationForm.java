package main.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ModerationForm {
    @JsonProperty("post_id") private long postId;
    @JsonProperty("decision") private String decision;
}
