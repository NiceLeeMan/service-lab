package spring.servicelabbackend.user.controller;

public record SocialSignupReq(
        String nickname,
        String phoneNumber,
        String email
) {
}
