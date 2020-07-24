package main.service;

import main.model.entity.GlobalSetting;
import main.model.enums.SettingsCode;

import java.util.List;

public interface GlobalSettingsService {
    boolean settingMultiUserModeIsEnabled();

    boolean settingPostPreModerationIsEnabled();

    boolean settingStatisticsIsPublicIsEnabled();

    void setValue(SettingsCode code, boolean value);

    boolean settingIsEnabled(SettingsCode code);

    List<GlobalSetting> findAll();
}
