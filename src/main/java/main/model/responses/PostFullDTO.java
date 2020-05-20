package main.model.responses;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PostFullDTO extends PostInfoDTO{
    private List<CommentDTO> comments;
    private List<String> tags;

    public PostFullDTO(PostSimpleDTO postSimpleDTO, PostInfoDTO postInfoDTO) {
        super(postSimpleDTO, postInfoDTO);
    }
}
