package main.repositories;

import main.model.entities.GlobalSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GlobalSettingsRepository extends JpaRepository<GlobalSettings, Long> {
}
