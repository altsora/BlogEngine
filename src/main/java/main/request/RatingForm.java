package main.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RatingForm {
    @JsonProperty("post_id") private int postId;
    // С одним полем десериализация не работает, потому добавлено второе поле для решения проблемы
    private String workaround;
}
