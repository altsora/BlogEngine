package main.repositories;

import main.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

//@Repository
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

    @Query("SELECT u FROM User u WHERE u.email = :email")
    User findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.code = :code")
    User findByCode(@Param("code") String code);
}
