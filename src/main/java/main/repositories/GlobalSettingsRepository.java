package main.repositories;

import main.model.entities.GlobalSettings;
import main.model.entities.enums.SettingsCodeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GlobalSettingsRepository extends JpaRepository<GlobalSettings, Long> {
    String MULTI_USER_MODE = "MULTIUSER_MODE";
    String POST_PRE_MODERATION = "POST_PREMODERATION";
    String STATISTICS_IS_PUBLIC = "STATISTICS_IS_PUBLIC";

    //==================================================================================================================

    @Query("SELECT gs FROM GlobalSettings gs " +
            "WHERE " +
            "   gs.code = '" + MULTI_USER_MODE + "' AND " +
            "   gs.value = 'YES'")
    GlobalSettings allowedMultiUserMode();

    @Query("SELECT gs FROM GlobalSettings gs " +
            "WHERE " +
            "   gs.code = '" + POST_PRE_MODERATION + "' AND " +
            "   gs.value = 'YES'")
    GlobalSettings allowedPostPreModeration();

    @Query("SELECT gs FROM GlobalSettings gs " +
            "WHERE " +
            "   gs.code = '" + STATISTICS_IS_PUBLIC + "' AND " +
            "   gs.value = 'YES'")
    GlobalSettings allowedStatisticsIsPublic();
}
