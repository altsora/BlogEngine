package main.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostForm {
    @JsonProperty("time") private String time;
    @JsonProperty("active") private int active;
    @JsonProperty("title") private String title;
    @JsonProperty("tags") private List<String> tags;
    @JsonProperty("text") private String text;
}
