package spring.servicelabbackend.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String nickname;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false, unique = true, length = 20)
    private String email;

    // 해시된 비밀번호 저장
    @Column(nullable = false, length = 255)
    private String password;

    @Builder
    public User(
            String nickname,
            String phoneNumber,
            String email,
            String password
    ) {
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
    }
}