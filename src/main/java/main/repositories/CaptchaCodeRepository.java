package main.repositories;

import main.model.entity.CaptchaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CaptchaCodeRepository extends JpaRepository<CaptchaCode, Long> {
    @Query("SELECT c FROM CaptchaCode c WHERE c.secretCode = :secretCode")
    CaptchaCode findBySecretCode(@Param("secretCode") String secretCode);

    @Query("SELECT c.code FROM CaptchaCode c WHERE c.secretCode = :secretCode")
    String getCodeBySecretCode(@Param("secretCode") String secretCode);
}
