package io.fptu.sep490.repository;

import io.fptu.sep490.model.Account;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    boolean existsByEmail(@NotBlank(message = "signup.email.required") @Email(message = "signup.email.invalid") String email);

    boolean existsByUsername(@NotBlank(message = "signup.name.required") String userName);

    @Query("SELECT a FROM Account a WHERE a.username = :input OR a.email = :input")
    Optional<Account> findByUsernameOrEmail(@Param("input") String input);
}
