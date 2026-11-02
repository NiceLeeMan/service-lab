package spring.servicelabbackend.user.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.servicelabbackend.global.BusinessException;
import spring.servicelabbackend.user.User;
import spring.servicelabbackend.user.UserRepository;
import spring.servicelabbackend.user.controller.UserSignupReq;

import java.util.regex.Pattern;

import static spring.servicelabbackend.user.UserErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserSignupService {

    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^01[016789]-?\\d{3,4}-?\\d{4}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*[^A-Za-z0-9]).{8,20}$");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(UserSignupReq request) {
        validate(request);

        if (userRepository.existsByNickname(request.nickname())) {
            throw new BusinessException(NICKNAME_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(EMAIL_ALREADY_EXISTS);
        }

        User user = User.builder()
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .phoneNumber(request.phoneNumber())
                .email(request.email())
                .build();

        userRepository.save(user);
    }

    private void validate(UserSignupReq request) {
        if (request.nickname() == null || request.nickname().isBlank()) {
            throw new BusinessException(NICKNAME_REQUIRED);
        }
        if (request.phoneNumber() == null || request.phoneNumber().isBlank()) {
            throw new BusinessException(PHONE_NUMBER_REQUIRED);
        }
        if (request.email() == null || request.email().isBlank()) {
            throw new BusinessException(EMAIL_REQUIRED);
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new BusinessException(PASSWORD_REQUIRED);
        }
        if (request.nickname().length() > 20) {
            throw new BusinessException(INVALID_NICKNAME_LENGTH);
        }
        if (request.phoneNumber().length() > 20) {
            throw new BusinessException(INVALID_PHONE_NUMBER_LENGTH);
        }
        if (!PHONE_NUMBER_PATTERN.matcher(request.phoneNumber()).matches()) {
            throw new BusinessException(INVALID_PHONE_NUMBER_FORMAT);
        }
        if (request.email().length() > 20) {
            throw new BusinessException(INVALID_EMAIL_LENGTH);
        }
        if (!EMAIL_PATTERN.matcher(request.email()).matches()) {
            throw new BusinessException(INVALID_EMAIL_FORMAT);
        }
        if (request.password().length() < 8 || request.password().length() > 20) {
            throw new BusinessException(INVALID_PASSWORD_LENGTH);
        }
        if (!PASSWORD_PATTERN.matcher(request.password()).matches()) {
            throw new BusinessException(INVALID_PASSWORD_FORMAT);
        }
    }
}
