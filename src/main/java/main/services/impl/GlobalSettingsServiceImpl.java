package main.services.impl;

import lombok.RequiredArgsConstructor;
import main.api.responses.StatisticResponse;
import main.model.entities.GlobalSetting;
import main.model.enums.SettingsCode;
import main.model.enums.SettingsValue;
import main.repositories.GlobalSettingsRepository;
import main.services.GlobalSettingsService;
import org.springframework.stereotype.Service;

import java.util.List;

import static main.model.enums.SettingsCode.*;
import static main.model.enums.SettingsValue.NO;
import static main.model.enums.SettingsValue.YES;

@Service
@RequiredArgsConstructor
public class GlobalSettingsServiceImpl implements GlobalSettingsService {
    private final GlobalSettingsRepository globalSettingsRepository;

    //==================================================================================================================

    @Override
    public boolean settingMultiUserModeIsEnabled() {
        return settingIsEnabled(MULTIUSER_MODE);
    }

    @Override
    public boolean settingPostPreModerationIsEnabled() {
        return settingIsEnabled(POST_PREMODERATION);
    }

    @Override
    public boolean settingStatisticsIsPublicIsEnabled() {
        return settingIsEnabled(STATISTICS_IS_PUBLIC);
    }

    @Override
    public void setValue(SettingsCode settingsCode, boolean value) {
        if (settingIsEnabled(settingsCode) != value) {
            SettingsValue newValue = value ? YES : NO;
            GlobalSetting globalSetting = globalSettingsRepository.findSettingByCode(settingsCode);
            globalSetting.setValue(newValue);
            globalSettingsRepository.saveAndFlush(globalSetting);
        }
    }

    @Override
    public boolean settingIsEnabled(SettingsCode code) {
        return globalSettingsRepository.checkValue(code, YES) != null;
    }

    @Override
    public List<GlobalSetting> findAll() {
        return globalSettingsRepository.findAll();
    }

    @Override
    public StatisticResponse getStatisticResponse(int dislikesCount, int likesCount, int postsCount, int viewsCount, long firstPublication) {
        return StatisticResponse.builder()
                .dislikesCount(dislikesCount)
                .firstPublication(firstPublication)
                .likesCount(likesCount)
                .postsCount(postsCount)
                .viewsCount(viewsCount)
                .build();
    }
}
