package main.model.repositories;

import main.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from #{#entityName} u where u.id = 1")
    User findByPostId();
}
