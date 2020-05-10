package main.model.responses;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class UserSimple implements ResponseDTO {
    private int id;
    private String name;

    public UserSimple(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
