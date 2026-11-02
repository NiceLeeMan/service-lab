package spring.servicelabbackend.user.controller;

public record UserSignupReq(
        String password,
        String nickname,
        String phoneNumber,
        String email
) {
}
