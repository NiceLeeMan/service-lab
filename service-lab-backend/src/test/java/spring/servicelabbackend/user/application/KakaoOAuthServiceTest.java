package spring.servicelabbackend.user.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import spring.servicelabbackend.user.outbound.kakao.KakaoOAuthTokenException;
import spring.servicelabbackend.user.outbound.kakao.KakaoOAuthTokenRes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class KakaoOAuthServiceTest {

    private MockRestServiceServer server;
    private KakaoOAuthService service;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl("https://kauth.kakao.com");
        server = MockRestServiceServer.bindTo(builder).build();
        service = new KakaoOAuthService(
                "rest-api-key",
                "client-secret",
                "http://localhost:8080/api/auth/kakao/callback",
                builder.build()
        );
    }

    @Test
    void exchangesAuthorizationCodeForIdToken() {
        server.expect(once(), requestTo("https://kauth.kakao.com/oauth/token"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(content().string(org.hamcrest.Matchers.allOf(
                        org.hamcrest.Matchers.containsString("grant_type=authorization_code"),
                        org.hamcrest.Matchers.containsString("client_id=rest-api-key"),
                        org.hamcrest.Matchers.containsString("code=authorization-code"),
                        org.hamcrest.Matchers.containsString("client_secret=client-secret")
                )))
                .andRespond(withSuccess("""
                        {
                          "token_type": "bearer",
                          "access_token": "access-token",
                          "id_token": "id-token",
                          "expires_in": 7199,
                          "refresh_token": "refresh-token",
                          "refresh_token_expires_in": 86399,
                          "scope": "openid"
                        }
                        """, MediaType.APPLICATION_JSON));

        KakaoOAuthTokenRes response = service.exchangeCodeForTokens("authorization-code");

        assertThat(response.idToken()).isEqualTo("id-token");
        assertThat(response.accessToken()).isEqualTo("access-token");
        server.verify();
    }

    @Test
    void rejectsTokenResponseWithoutIdToken() {
        server.expect(once(), requestTo("https://kauth.kakao.com/oauth/token"))
                .andRespond(withSuccess("""
                        {
                          "token_type": "bearer",
                          "access_token": "access-token",
                          "expires_in": 7199,
                          "refresh_token": "refresh-token",
                          "refresh_token_expires_in": 86399
                        }
                        """, MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> service.exchangeCodeForTokens("authorization-code"))
                .isInstanceOf(KakaoOAuthTokenException.class)
                .hasMessageContaining("ID 토큰");
        server.verify();
    }
}
