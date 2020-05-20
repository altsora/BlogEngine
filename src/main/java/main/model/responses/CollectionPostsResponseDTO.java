package main.model.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionPostsResponseDTO<T extends ResponseDTO> implements ResponseDTO{

    @JsonProperty("count")
    private long count;

    @JsonProperty("posts")
    private Collection<T> posts;
}
