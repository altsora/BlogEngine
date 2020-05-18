package main.model.responses;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentDTO {
    private int id;
    private String time;
    private String text;
    private UserWithPhoto user;
}
