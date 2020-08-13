package main.services;

import main.api.responses.StatisticResponse;
import main.model.entities.GlobalSetting;
import main.model.enums.SettingsCode;

import java.util.List;

public interface GlobalSettingsService {
    boolean settingIsEnabled(SettingsCode code);

    boolean settingMultiUserModeIsEnabled();

    boolean settingPostPreModerationIsEnabled();

    boolean settingStatisticsIsPublicIsEnabled();

    void setValue(SettingsCode code, boolean value);

    List<GlobalSetting> findAll();

    StatisticResponse getStatisticResponse(int dislikesCount, int likesCount, int postsCount, int viewsCount, long firstPublication);
}
