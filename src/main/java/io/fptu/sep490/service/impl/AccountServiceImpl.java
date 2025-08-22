package io.fptu.sep490.service.impl;

import io.fptu.sep490.config.security.filter.UserDetailsImpl;
import io.fptu.sep490.constant.ActivityStatus;
import io.fptu.sep490.constant.EmailType;
import io.fptu.sep490.constant.ErrorCode;
import io.fptu.sep490.dto.request.*;
import io.fptu.sep490.dto.response.BaseResponse;
import io.fptu.sep490.dto.response.OtpResponse;
import io.fptu.sep490.dto.response.UserDetailResponse;
import io.fptu.sep490.exception.CustomBusinessException;
import io.fptu.sep490.model.Account;
import io.fptu.sep490.model.enums.Role;
import io.fptu.sep490.repository.AccountRepository;
import io.fptu.sep490.service.AccountService;
import io.fptu.sep490.service.OtpService;
import io.fptu.sep490.service.TokenStoreService;
import io.fptu.sep490.utils.*;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final TokenStoreService tokenStoreService;
    private final EmailUtils emailUtils;
    private final OtpService otpService;

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
        var accessToken = jwtUtils.generateAccessToken(userDetails);
        var refreshToken = jwtUtils.generateRefreshToken(userDetails);
        var role = UserInfoUtils.getCurrentUserRole();

        tokenStoreService.storeAccessToken(accessToken, jwtUtils.getAccessTokenExpirationMs());
        tokenStoreService.storeRefreshToken(refreshToken, jwtUtils.getRefreshTokenExpirationMs());

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
                || !jwtUtils.isRefreshToken(request.getRefreshToken())) {
            throw new CustomBusinessException(ErrorCode.VAL_PARAM_INVALID.getCode(),
                    LocalizedTextUtils.getLocalizedText("auth.token.invalid"));
        }

        if (jwtUtils.isTokenExpired(request.getRefreshToken())) {
            tokenStoreService.revokeRefreshToken(request.getRefreshToken());
            throw new CustomBusinessException(ErrorCode.VAL_PARAM_INVALID.getCode(),
                    LocalizedTextUtils.getLocalizedText("auth.token.expired"));
        }

        var accountId = jwtUtils.extractAccountId(request.getRefreshToken());
        var account = accountRepository.findById(accountId)
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.BUS_NOT_FOUND.getCode(),
                        LocalizedTextUtils.getLocalizedText("user.not.found")));

        var userDetails = UserDetailsImpl.builder()
                .account(account)
                .build();

        var newAccessToken = jwtUtils.generateAccessToken(userDetails);
        var refreshToken = jwtUtils.generateRefreshToken(userDetails);
        tokenStoreService.storeAccessToken(newAccessToken, jwtUtils.getAccessTokenExpirationMs());
        tokenStoreService.storeRefreshToken(refreshToken, jwtUtils.getRefreshTokenExpirationMs());

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

    @Override
    public OtpResponse checkOtp(OtpRequest request) {
        var email = request.getEmail();
        var otp = request.getOtp();

        if (otp.isEmpty()) {
            throw new CustomBusinessException(
                    ErrorCode.VAL_PARAM_INVALID.getCode(),
                    LocalizedTextUtils.getLocalizedText(
                            "forgot-pass.otp.not-exist"
                    )
            );
        }

        var check = otpService.verifyOtp(email, otp);
        if (!check) {
            throw new CustomBusinessException(
                    ErrorCode.BUS_GENERIC_ERROR.getCode(),
                    LocalizedTextUtils.getLocalizedText(
                            "check-otp.invalid"
                    )
            );
        }

        otpService.saveOtpVerified(email);

        return OtpResponse.builder()
                .email(email)
                .build();
    }

    @Override
    public OtpResponse sendOtp(OtpRequest request) throws MessagingException {

        var account = accountRepository.findByEmail(request.getEmail());

        if (!otpService.checkOtpLimit(request.getEmail())) {
            throw new CustomBusinessException(
                    ErrorCode.BUS_GENERIC_ERROR.getCode(),
                    LocalizedTextUtils.getLocalizedText("send-otp.limit.invalid")
            );
        }

        if (account.isEmpty()) {
            throw new CustomBusinessException(
                    ErrorCode.BUS_NOT_FOUND.getCode(),
                    LocalizedTextUtils.getLocalizedText("forgot-pass.email.not-exist")
            );
        }

        if (otpService.isOtpLocked(request.getEmail())) {
            throw new CustomBusinessException(
                    ErrorCode.SYS_ILLEGAL_ACCESS.getCode(),
                    LocalizedTextUtils.getLocalizedText("forgot-pass.otp.too-soon")
            );
        }

        var otp = GeneratorUtils.generateRandomOTP(6);
        otpService.saveOtp(request.getEmail(), otp);


        Map<String, Object> vars = Map.of(
                "name", account.get().getUsername(),
                "otp", otp,
                "expiry", "2 phút"
        );


        emailUtils.sendEmail(
                request.getEmail(),
                EmailType.SEND_OTP,
                vars
        );

        otpService.lockOtpRequest(request.getEmail());

        return OtpResponse.builder()
                .email(request.getEmail())
                .otpTime(120)
                .duration(60)
                .build();
    }

    @Override
    public BaseResponse<?> resetPassword(ResetPassRequest request) {

        var account = accountRepository.findByEmail(request.getEmail());

        if (account.isEmpty()) {
            throw new CustomBusinessException(
                    ErrorCode.BUS_NOT_FOUND.getCode(),
                    LocalizedTextUtils.getLocalizedText("forgot-pass.email.not-exist")
            );
        }

        if (!otpService.isOtpVerified(request.getEmail())) {
            throw new CustomBusinessException(ErrorCode.SYS_ILLEGAL_ACCESS.getCode(),
                    LocalizedTextUtils.getLocalizedText("access.denied")
            );
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new CustomBusinessException(ErrorCode.VAL_PARAM_INVALID.getCode()
                    , LocalizedTextUtils.getLocalizedText("signup.password.confirm.invalid"));
        }

        account.get().setPassword(passwordEncoder.encode(request.getPassword()));

        otpService.clearOtpVerified(request.getEmail());

        accountRepository.save(account.get());

        return BaseResponse.builder().build();
    }


}