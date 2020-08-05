package main.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SettingsForm {
    @JsonProperty("MULTIUSER_MODE") private boolean multiUserModeValue;
    @JsonProperty("POST_PREMODERATION") private boolean postPreModerationValue;
    @JsonProperty("STATISTICS_IS_PUBLIC") private boolean statisticsIsPublicValue;
}
