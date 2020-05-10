package main.model.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Collection;

@Data
public class CollectionPostsResponseDTO<T extends ResponseDTO> implements ResponseDTO{

    @JsonProperty("count")
    private long count;

    @JsonProperty("posts")
    private Collection<T> posts;
}
