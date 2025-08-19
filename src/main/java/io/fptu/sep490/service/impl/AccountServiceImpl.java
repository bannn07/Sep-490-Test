package io.fptu.sep490.service.impl;

import io.fptu.sep490.constant.ActivityStatus;
import io.fptu.sep490.constant.ErrorCode;
import io.fptu.sep490.dto.request.LoginRequest;
import io.fptu.sep490.dto.request.RefreshTokenRequest;
import io.fptu.sep490.dto.request.RegisterRequest;
import io.fptu.sep490.dto.response.UserDetailResponse;
import io.fptu.sep490.exception.CustomBusinessException;
import io.fptu.sep490.model.Account;
import io.fptu.sep490.model.enums.Role;
import io.fptu.sep490.repository.AccountRepository;
import io.fptu.sep490.config.security.filter.UserDetailsImpl;
import io.fptu.sep490.service.AccountService;
import io.fptu.sep490.service.TokenStoreService;
import io.fptu.sep490.utils.JwtUtils;
import io.fptu.sep490.utils.LocalizedTextUtils;
import io.fptu.sep490.utils.UserInfoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenStoreService tokenStoreService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDetailResponse registerUser(RegisterRequest request) {

        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new CustomBusinessException(ErrorCode.BUS_DUPLICATE.getCode()
                    , LocalizedTextUtils.getLocalizedText("signup.email.duplicate"));
        }

        if (accountRepository.existsByUsername(request.getUserName())) {
            throw new CustomBusinessException(ErrorCode.BUS_DUPLICATE.getCode()
                    , LocalizedTextUtils.getLocalizedText("signup.name.duplicate"));
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new CustomBusinessException(ErrorCode.VAL_PARAM_INVALID.getCode()
                    , LocalizedTextUtils.getLocalizedText("signup.password.confirm.invalid"));
        }

        var role = UserInfoUtils.getCurrentUserRole();
        var isAdmin = Role.ADMIN.name().equals(role);

        var account = Account.builder()
                .username(request.getUserName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(ActivityStatus.ACTIVE.isStatus())
                .locked(ActivityStatus.UNLOCKED.isStatus())
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

    @Override
    public UserDetailResponse loginUser(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getInput(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        var userDetails = (UserDetailsImpl) authentication.getPrincipal();
        var accessToken = jwtService.generateAccessToken(userDetails);
        var refreshToken = jwtService.generateRefreshToken(userDetails);
        var role = UserInfoUtils.getCurrentUserRole();

        tokenStoreService.storeAccessToken(accessToken, jwtService.getAccessTokenExpirationMs());
        tokenStoreService.storeRefreshToken(refreshToken, jwtService.getRefreshTokenExpirationMs());

        return UserDetailResponse.builder()
                .id(userDetails.account().getId())
                .email(userDetails.account().getEmail())
                .username(userDetails.account().getUsername())
                .enabled(userDetails.isEnabled())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(role)
                .build();
    }

    @Override
    public UserDetailResponse refreshAccessToken(RefreshTokenRequest request) {

        if (!tokenStoreService.isRefreshTokenValid(request.getRefreshToken())
                || !jwtService.isRefreshToken(request.getRefreshToken())) {
            throw new CustomBusinessException(ErrorCode.VAL_PARAM_INVALID.getCode(),
                    LocalizedTextUtils.getLocalizedText("auth.token.invalid"));
        }

        if (jwtService.isTokenExpired(request.getRefreshToken())) {
            tokenStoreService.revokeRefreshToken(request.getRefreshToken());
            throw new CustomBusinessException(ErrorCode.VAL_PARAM_INVALID.getCode(),
                    LocalizedTextUtils.getLocalizedText("auth.token.expired"));
        }

        var accountId = jwtService.extractAccountId(request.getRefreshToken());
        var account = accountRepository.findById(accountId)
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.BUS_NOT_FOUND.getCode(),
                        LocalizedTextUtils.getLocalizedText("user.not.found")));

        var userDetails = UserDetailsImpl.builder()
                .account(account)
                .build();

        var newAccessToken = jwtService.generateAccessToken(userDetails);
        var refreshToken = jwtService.generateRefreshToken(userDetails);
        tokenStoreService.storeAccessToken(newAccessToken, jwtService.getAccessTokenExpirationMs());
        tokenStoreService.storeRefreshToken(refreshToken, jwtService.getRefreshTokenExpirationMs());

        return UserDetailResponse.builder()
                .id(account.getId())
                .email(account.getEmail())
                .username(account.getUsername())
                .enabled(userDetails.isEnabled())
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .role(account.getRole().name())
                .build();
    }

}