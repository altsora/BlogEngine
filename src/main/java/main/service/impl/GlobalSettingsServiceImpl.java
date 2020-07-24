package main.service.impl;

import lombok.RequiredArgsConstructor;
import main.model.entity.GlobalSetting;
import main.model.enums.SettingsCode;
import main.model.enums.SettingsValue;
import main.repository.GlobalSettingsRepository;
import main.service.GlobalSettingsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GlobalSettingsServiceImpl implements GlobalSettingsService {
    private final GlobalSettingsRepository globalSettingsRepository;

    //==================================================================================================================

    @Override
    public boolean settingMultiUserModeIsEnabled() {
        return settingIsEnabled(SettingsCode.MULTIUSER_MODE);
    }

    @Override
    public boolean settingPostPreModerationIsEnabled() {
        return settingIsEnabled(SettingsCode.POST_PREMODERATION);
    }

    @Override
    public boolean settingStatisticsIsPublicIsEnabled() {
        return settingIsEnabled(SettingsCode.STATISTICS_IS_PUBLIC);
    }

    @Override
    public void setValue(SettingsCode settingsCode, boolean value) {
        if (settingIsEnabled(settingsCode) != value) {
            SettingsValue newValue = value ? SettingsValue.YES : SettingsValue.NO;
            GlobalSetting globalSetting = globalSettingsRepository.findSettingByCode(settingsCode);
            globalSetting.setValue(newValue);
            globalSettingsRepository.saveAndFlush(globalSetting);
        }
    }

    @Override
    public boolean settingIsEnabled(SettingsCode code) {
        return globalSettingsRepository.checkValue(code, SettingsValue.YES) != null;
    }

    @Override
    public List<GlobalSetting> findAll() {
        return globalSettingsRepository.findAll();
    }
}
