package main.api.responses;

import lombok.Getter;

@Getter
public class UserWithPhotoResponse extends UserSimpleResponse {
    private String photo;

    public UserWithPhotoResponse(long id, String name, String photo) {
        super(id, name);
        this.photo = photo;
    }
}
