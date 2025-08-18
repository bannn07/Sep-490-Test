package io.fptu.sep490.config.datasourse;

import io.fptu.sep490.utils.UserInfoUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            try {
                String username = UserInfoUtils.getCurrentUser().account().getUsername();
                return Optional.of(username != null ? username : "SYSTEM");
            } catch (Exception e) {
                return Optional.of("SYSTEM");
            }
        };
    }

}