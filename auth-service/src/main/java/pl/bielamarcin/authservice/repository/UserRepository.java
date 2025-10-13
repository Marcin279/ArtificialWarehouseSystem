package pl.bielamarcin.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.bielamarcin.authservice.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
