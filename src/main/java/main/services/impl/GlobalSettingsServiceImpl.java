package main.services.impl;

import main.repositories.GlobalSettingsRepository;
import main.services.GlobalSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GlobalSettingsServiceImpl implements GlobalSettingsService {
    private GlobalSettingsRepository globalSettingsRepository;

    @Autowired
    public GlobalSettingsServiceImpl(GlobalSettingsRepository globalSettingsRepository) {
        this.globalSettingsRepository = globalSettingsRepository;
    }

    @Override
    public boolean allowedMultiUserMode() {
        return globalSettingsRepository.allowedMultiUserMode() != null;
    }

    @Override
    public boolean allowedPostPreModeration() {
        return globalSettingsRepository.allowedPostPreModeration() != null;
    }

    @Override
    public boolean allowedStatisticsIsPublic() {
        return globalSettingsRepository.allowedStatisticsIsPublic() != null;
    }
}
