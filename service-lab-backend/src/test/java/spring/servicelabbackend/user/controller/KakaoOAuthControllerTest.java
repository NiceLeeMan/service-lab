package spring.servicelabbackend.user.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.util.ReflectionTestUtils;
import spring.servicelabbackend.user.application.KakaoOAuthService;
import spring.servicelabbackend.user.User;
import spring.servicelabbackend.user.UserRepository;
import spring.servicelabbackend.user.outbound.kakao.KakaoOAuthTokenRes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KakaoOAuthControllerTest {

    private KakaoOAuthService service;
    private UserRepository userRepository;
    private KakaoOAuthController controller;

    @BeforeEach
    void setUp() {
        service = mock(KakaoOAuthService.class);
        userRepository = mock(UserRepository.class);
        controller = new KakaoOAuthController(service, userRepository);
        ReflectionTestUtils.setField(
                controller,
                "frontendRedirectUri",
                "http://localhost:5173/"
        );
    }

    @Test
    void exchangesCodeAndRedirectsToFrontendOnSuccess() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("KAKAO_OAUTH_STATE", "expected-state");
        session.setAttribute("KAKAO_OAUTH_NONCE", "expected-nonce");
        when(service.exchangeCodeForTokens("authorization-code"))
                .thenReturn(new KakaoOAuthTokenRes(
                        "bearer", "access-token", "id-token", 7199L,
                        "refresh-token", 86399L, "openid"
                ));
        when(service.extractEmail("id-token")).thenReturn("member@example.com");
        when(userRepository.existsByEmail("member@example.com")).thenReturn(true);

        ResponseEntity<Void> response = controller.callback(
                "authorization-code",
                "expected-state",
                null,
                session
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation())
                .hasToString("http://localhost:5173/?oauth=kakao&status=success");
        assertThat(session.getAttribute("KAKAO_OAUTH_STATE")).isNull();
        assertThat(session.getAttribute("KAKAO_OAUTH_NONCE")).isNull();
        verify(service).exchangeCodeForTokens("authorization-code");
    }

    @Test
    void redirectsUnknownUserToSocialSignup() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("KAKAO_OAUTH_STATE", "expected-state");
        when(service.exchangeCodeForTokens("authorization-code"))
                .thenReturn(new KakaoOAuthTokenRes(
                        "bearer", "access-token", "id-token", 7199L,
                        "refresh-token", 86399L, "openid"
                ));
        when(service.extractEmail("id-token")).thenReturn("new@example.com");
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);

        ResponseEntity<Void> response = controller.callback(
                "authorization-code",
                "expected-state",
                null,
                session
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation())
                .hasToString("http://localhost:5173/social-signup");
        assertThat(session.getAttribute("SOCIAL_SIGNUP_EMAIL"))
                .isEqualTo("new@example.com");
    }

    @Test
    void createsSocialUserWithoutPassword() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("SOCIAL_SIGNUP_EMAIL", "new@example.com");

        ResponseEntity<Void> response = controller.socialSignup(
                new SocialSignupReq("nickname", "010-1234-5678", "new@example.com"),
                session
        );

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getEmail()).isEqualTo("new@example.com");
        assertThat(userCaptor.getValue().getPassword()).isNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(session.getAttribute("SOCIAL_SIGNUP_EMAIL")).isNull();
    }

    @Test
    void rejectsCallbackWhenStateDoesNotMatch() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("KAKAO_OAUTH_STATE", "expected-state");

        ResponseEntity<Void> response = controller.callback(
                "authorization-code",
                "different-state",
                null,
                session
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation())
                .hasToString(
                        "http://localhost:5173/?oauth=kakao&status=error&reason=invalid_state"
                );
        verify(service, never()).exchangeCodeForTokens("authorization-code");
    }
}
