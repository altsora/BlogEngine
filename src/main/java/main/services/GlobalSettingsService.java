package main.services;

import main.model.entities.GlobalSetting;
import main.model.entities.enums.SettingsCodeType;

import java.util.List;

public interface GlobalSettingsService {
    boolean settingMultiUserModeIsEnabled();
    boolean settingPostPreModerationIsEnabled();
    boolean settingStatisticsIsPublicIsEnabled();

    GlobalSetting getSettingByCode(SettingsCodeType settingsCodeType);

    List<GlobalSetting> findAll();
}
