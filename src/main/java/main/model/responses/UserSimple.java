package main.model.responses;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserSimple implements ResponseDTO {
    private int id;
    private String name;

    public UserSimple(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
