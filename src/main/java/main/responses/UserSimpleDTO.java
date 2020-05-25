package main.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSimpleDTO implements ResponseDTO {
    private long id;
    private String name;
}
