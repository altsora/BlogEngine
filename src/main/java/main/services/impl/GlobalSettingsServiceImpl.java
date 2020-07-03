package main.services.impl;

import lombok.RequiredArgsConstructor;
import main.model.entities.GlobalSetting;
import main.model.enums.SettingsCode;
import main.model.enums.SettingsValue;
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
    public void setValue(SettingsCode settingsCode, boolean value) {
        SettingsValue valueType = value ? SettingsValue.YES : SettingsValue.NO;
        GlobalSetting globalSetting = globalSettingsRepository.findSettingByCode(settingsCode);
        globalSetting.setValue(valueType);
        globalSettingsRepository.saveAndFlush(globalSetting);
    }

    @Override
    public List<GlobalSetting> findAll() {
        return globalSettingsRepository.findAll();
    }
}
