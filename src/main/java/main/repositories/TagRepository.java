package main.repositories;

import main.model.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * Запрос возвращает список тэгов, содержащих указанное слово.
     * @param query - строка, которую содержат тэги из результирующей выборки.
     * @return - возвращает коллекцию тэгов, содержащих указанную строку.
     */
    @Query("SELECT t FROM Tag t WHERE t.name LIKE :query%")
    List<Tag> findAllTagsByQuery(@Param("query") String query);
}
