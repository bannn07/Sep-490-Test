package io.fptu.sep490.utils;

import io.fptu.sep490.model.Account;
import io.fptu.sep490.config.security.UserDetailsImpl;
import io.jsonwebtoken.lang.Strings;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserInfoUtils {
    public static UserDetailsImpl getCurrentUser() {
        if ("anonymousUser"
                .equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())) {

            return UserDetailsImpl.builder()
                    .account(
                            Account.builder()
                                    .username("SYSTEM")
                                    .build()
                    )
                    .build();
        }

        return (UserDetailsImpl)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static String getCurrentUserRole() {
        if ("anonymousUser"
                .equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())) {
            return Strings.EMPTY;
        }
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(Strings.EMPTY);
    }
}
