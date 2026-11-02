package spring.servicelabbackend.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.servicelabbackend.user.application.UserSignupService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserSignupService userSignupService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody UserSignupReq request) {
        userSignupService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
