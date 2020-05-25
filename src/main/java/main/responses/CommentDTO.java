package main.responses;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentDTO {
    private long id;
    private String time;
    private String text;
    private UserWithPhotoDTO user;
}
