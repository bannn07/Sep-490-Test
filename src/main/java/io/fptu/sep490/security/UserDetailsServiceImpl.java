package io.fptu.sep490.security;

import io.fptu.sep490.model.Account;
import io.fptu.sep490.repository.AccountRepository;
import io.fptu.sep490.utils.LocalizedTextUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        if (input == null)
            throw new UsernameNotFoundException(LocalizedTextUtils.getLocalizedText("username.not.empty"));

        Account user = (Account) accountRepository.findByUsernameOrEmail(input)
                .orElseThrow(() -> new UsernameNotFoundException(LocalizedTextUtils.getLocalizedText("user.not.found")));

        return new UserDetailsImpl(user);
    }


}
