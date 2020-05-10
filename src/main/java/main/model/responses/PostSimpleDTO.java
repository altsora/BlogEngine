package main.model.responses;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostSimpleDTO implements ResponseDTO{
    private int id;
    private String time;
    private UserSimple user;
    private String title;
    private String announce;

    public PostSimpleDTO(int id, String time, UserSimple user, String title, String announce) {
        this.id = id;
        this.time = time;
        this.user = user;
        this.title = title;
        this.announce = announce;
    }
}
