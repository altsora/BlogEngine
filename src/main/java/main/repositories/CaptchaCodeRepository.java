package main.repositories;

import main.model.entities.CaptchaCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaptchaCodeRepository extends JpaRepository<CaptchaCode, Long> {
}
