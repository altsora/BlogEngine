package main.model.responses;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostSimpleDTO implements ResponseDTO {
    private int id;
    private String time;
    private UserSimple user;
    private String title;
    private String announce;
}
