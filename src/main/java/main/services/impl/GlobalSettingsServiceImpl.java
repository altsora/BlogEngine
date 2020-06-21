package main.services.impl;

import lombok.RequiredArgsConstructor;
import main.model.entities.GlobalSetting;
import main.model.enums.SettingsCodeType;
import main.model.enums.SettingsValueType;
import main.repositories.GlobalSettingsRepository;
import main.services.GlobalSettingsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GlobalSettingsServiceImpl implements GlobalSettingsService {
    private final GlobalSettingsRepository globalSettingsRepository;

    //==================================================================================================================

    @Override
    public boolean settingMultiUserModeIsEnabled() {
        return globalSettingsRepository.settingMultiUserModeIsEnabled() != null;
    }

    @Override
    public boolean settingPostPreModerationIsEnabled() {
        return globalSettingsRepository.settingPostPreModerationIsEnabled() != null;
    }

    @Override
    public boolean settingStatisticsIsPublicIsEnabled() {
        return globalSettingsRepository.settingStatisticsIsPublicIsEnabled() != null;
    }

    @Override
    public void setValue(SettingsCodeType settingsCodeType, boolean value) {
        SettingsValueType valueType = value ? SettingsValueType.YES : SettingsValueType.NO;
        GlobalSetting globalSetting = globalSettingsRepository.findSettingByCode(settingsCodeType);
        globalSetting.setValue(valueType);
        globalSettingsRepository.saveAndFlush(globalSetting);
    }

    @Override
    public List<GlobalSetting> findAll() {
        return globalSettingsRepository.findAll();
    }
}
