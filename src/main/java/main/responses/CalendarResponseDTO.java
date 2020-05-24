package main.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.json.simple.JSONObject;

import java.util.List;

@Data
@AllArgsConstructor
public class CalendarResponseDTO {
    List<Integer> years;
    JSONObject posts;
}
