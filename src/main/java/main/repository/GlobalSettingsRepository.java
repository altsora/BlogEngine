package main.repository;

import main.model.entity.GlobalSetting;
import main.model.enums.SettingsCode;
import main.model.enums.SettingsValue;
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
            "   gs.code = :code AND " +
            "   gs.value = :value")
    GlobalSetting checkValue(SettingsCode code, SettingsValue value);

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

    /**
     * Запрос возвращает настройку по её коду.
     * @param settingsCode - код настройки;
     * @return - возвращается объект GlobalSetting по указанному коду.
     */
    @Query("SELECT gs FROM GlobalSetting gs WHERE gs.code = :settingCode")
    GlobalSetting findSettingByCode(@Param("settingCode") SettingsCode settingsCode);
}
