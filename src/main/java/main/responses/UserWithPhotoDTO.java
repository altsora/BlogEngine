package main.responses;

import lombok.Getter;

@Getter
public class UserWithPhotoDTO extends UserSimpleDTO {
    private String photo;

    public UserWithPhotoDTO(long id, String name, String photo) {
        super(id, name);
        this.photo = photo;
    }
}
