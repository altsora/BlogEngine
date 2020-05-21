package main.responses;

import lombok.Data;
import org.json.simple.JSONObject;

import java.util.List;

@Data
public class CalendarResponseDTO {
    List<Integer> years;
    JSONObject posts;
}
