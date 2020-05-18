package main.model.responses;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserWithPhoto extends UserSimple {
    private String photo;

    public UserWithPhoto(int id, String name, String photo) {
        super(id, name);
        this.photo = photo;
    }
}
