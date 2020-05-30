package main.services.impl;

import main.model.entities.GlobalSetting;
import main.model.enums.SettingsCodeType;
import main.model.enums.SettingsValueType;
import main.repositories.GlobalSettingsRepository;
import main.services.GlobalSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GlobalSettingsServiceImpl implements GlobalSettingsService {
    private GlobalSettingsRepository globalSettingsRepository;

    @Autowired
    public GlobalSettingsServiceImpl(GlobalSettingsRepository globalSettingsRepository) {
        this.globalSettingsRepository = globalSettingsRepository;
    }

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
    public GlobalSetting findSettingByCode(SettingsCodeType settingsCodeType) {
        return globalSettingsRepository.findSettingByCode(settingsCodeType);
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
