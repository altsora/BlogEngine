package main.services;

public interface GlobalSettingsService {
    boolean allowedMultiUserMode();
    boolean allowedPostPreModeration();
    boolean allowedStatisticsIsPublic();
}
