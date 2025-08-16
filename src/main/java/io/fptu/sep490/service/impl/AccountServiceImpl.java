package io.fptu.sep490.service.impl;

import io.fptu.sep490.constant.ActivityStatus;
import io.fptu.sep490.dto.request.RegisterRequest;
import io.fptu.sep490.dto.response.UserDetailResponse;
import io.fptu.sep490.model.Account;
import io.fptu.sep490.model.enums.Role;
import io.fptu.sep490.exception.DuplicateResourceException;
import io.fptu.sep490.exception.IllegalArgumentException;
import io.fptu.sep490.repository.AccountRepository;
import io.fptu.sep490.service.AccountService;
import io.fptu.sep490.utils.LocalizedTextUtils;
import io.fptu.sep490.utils.UserInfoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDetailResponse registerUser(RegisterRequest request) {

        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(LocalizedTextUtils.getLocalizedText("signup.email.duplicate"));
        }

        if (accountRepository.existsByUsername(request.getUserName())) {
            throw new DuplicateResourceException(LocalizedTextUtils.getLocalizedText("signup.name.duplicate"));
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException(LocalizedTextUtils.getLocalizedText("signup.password.confirm.invalid"));
        }

        var role = UserInfoUtils.getCurrentUserRole();
        var isAdmin = Role.ADMIN.name().equals(role);

        var account = Account.builder()
                .username(request.getUserName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(ActivityStatus.ACTIVE.isStatus())
                .role(isAdmin && request.getRole() != null
                        ? Role.valueOf(request.getRole())
                        : Role.USER)
                .build();

        accountRepository.save(account);

        return UserDetailResponse.builder()
                .id(account.getId())
                .email(account.getEmail())
                .username(account.getUsername())
                .enabled(account.isEnabled())
                .role(account.getRole().name())
                .build();
    }
    
}