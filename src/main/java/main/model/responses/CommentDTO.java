package main.model.responses;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentDTO {
    private int id;
    private LocalDateTime time;
    private String text;
    private UserWithPhoto userWithPhoto;
}
