package spring.servicelabbackend.user.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import spring.servicelabbackend.global.BusinessException;
import spring.servicelabbackend.user.User;
import spring.servicelabbackend.user.UserRepository;
import spring.servicelabbackend.user.controller.UserSignupReq;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserSignupServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserSignupService userSignupService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userSignupService = new UserSignupService(userRepository, passwordEncoder);
    }

    @Test
    void createsUserWithEncodedPassword() {
        UserSignupReq request = new UserSignupReq(
                "password!",
                "nickname",
                "010-1234-5678",
                "user@example.com"
        );
        when(passwordEncoder.encode("password!")).thenReturn("encoded-password");

        userSignupService.signup(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getEmail()).isEqualTo("user@example.com");
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("encoded-password");
    }

    @Test
    void rejectsDuplicateEmail() {
        UserSignupReq request = new UserSignupReq(
                "password!",
                "nickname",
                "010-1234-5678",
                "user@example.com"
        );
        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userSignupService.signup(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("이미 가입된 이메일입니다.");
    }
}
