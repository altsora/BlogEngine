package main.repositories;

import main.model.entities.GlobalSetting;
import main.model.entities.enums.SettingsCodeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GlobalSettingsRepository extends JpaRepository<GlobalSetting, Long> {
    String MULTI_USER_MODE = "MULTIUSER_MODE";
    String POST_PRE_MODERATION = "POST_PREMODERATION";
    String STATISTICS_IS_PUBLIC = "STATISTICS_IS_PUBLIC";

    //==================================================================================================================

    @Query("SELECT gs FROM GlobalSetting gs " +
            "WHERE " +
            "   gs.code = '" + MULTI_USER_MODE + "' AND " +
            "   gs.value = 'YES'")
    GlobalSetting settingMultiUserModeIsEnabled();

    @Query("SELECT gs FROM GlobalSetting gs " +
            "WHERE " +
            "   gs.code = '" + POST_PRE_MODERATION + "' AND " +
            "   gs.value = 'YES'")
    GlobalSetting settingPostPreModerationIsEnabled();

    @Query("SELECT gs FROM GlobalSetting gs " +
            "WHERE " +
            "   gs.code = '" + STATISTICS_IS_PUBLIC + "' AND " +
            "   gs.value = 'YES'")
    GlobalSetting settingStatisticsIsPublicIsEnabled();

    @Query("SELECT gs FROM GlobalSetting gs WHERE gs.code = :settingCode")
    GlobalSetting getSettingByCode(@Param("settingCode")SettingsCodeType settingsCodeType);
}
