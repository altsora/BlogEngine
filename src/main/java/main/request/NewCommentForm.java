package main.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NewCommentForm {
    @JsonProperty("post_id") private long postId;
    @JsonProperty("parent_id") private Object parentIdObj;
    @JsonProperty("text") private String text;
}
