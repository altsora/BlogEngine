package main.responses;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostSimpleDTO implements ResponseDTO {
    private int id;
    private String time;
    private UserSimpleDTO user;
    private String title;
    private String announce;

    public PostSimpleDTO(PostSimpleDTO postSimpleDTO) {
        this.id = postSimpleDTO.id;
        this.time = postSimpleDTO.time;
        this.user = postSimpleDTO.user;
        this.title = postSimpleDTO.title;
        this.announce = postSimpleDTO.announce;
    }
}
