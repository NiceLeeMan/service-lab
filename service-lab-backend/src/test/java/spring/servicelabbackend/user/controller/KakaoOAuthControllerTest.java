package spring.servicelabbackend.user.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.util.ReflectionTestUtils;
import spring.servicelabbackend.user.application.KakaoOAuthService;
import spring.servicelabbackend.user.outbound.kakao.KakaoOAuthTokenRes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KakaoOAuthControllerTest {

    private KakaoOAuthService service;
    private KakaoOAuthController controller;

    @BeforeEach
    void setUp() {
        service = mock(KakaoOAuthService.class);
        controller = new KakaoOAuthController(service);
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
