package main.services.impl;

import main.model.entities.GlobalSetting;
import main.model.entities.enums.SettingsCodeType;
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
    public GlobalSetting getSettingByCode(SettingsCodeType settingsCodeType) {
        return globalSettingsRepository.getSettingByCode(settingsCodeType);
    }

    @Override
    public List<GlobalSetting> findAll() {
        return globalSettingsRepository.findAll();
    }
}
