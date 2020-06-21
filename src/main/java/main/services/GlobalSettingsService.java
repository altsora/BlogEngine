package main.services;

import main.model.entities.GlobalSetting;
import main.model.enums.SettingsCodeType;

import java.util.List;

public interface GlobalSettingsService {
    boolean settingMultiUserModeIsEnabled();

    boolean settingPostPreModerationIsEnabled();

    boolean settingStatisticsIsPublicIsEnabled();


    void setValue(SettingsCodeType settingsCodeType, boolean value);

    List<GlobalSetting> findAll();
}
