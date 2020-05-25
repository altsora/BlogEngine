package main.repositories;

import main.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Запрос возвращает пользователя по указанным почте и паролю.
     * @param email - почта, по которой осуществляется поиск пользователя;
     * @param password - пароль, по которой осуществляется поиск пользователя;
     * @return - возвращает пользователя.
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.password = :password")
    User findByEmailAndPassword(
            @Param("email") String email,
            @Param("password") String password
    );

    /**
     * Зарос возвращает пользователя по его идентификатору.
     * @param userId - ID пользователя;
     * @return - возвращает пользователя.
     */
    @Query("SELECT u FROM User u WHERE u.id = :userId")
    User findByUserId(@Param("userId") long userId);
}
