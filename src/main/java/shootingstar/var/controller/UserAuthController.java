package shootingstar.var.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserAuthController {

    @DeleteMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok().body("로그아웃");
    }

    @DeleteMapping("/refresh")
    public ResponseEntity<String> refresh() {
        return ResponseEntity.ok().body("액세스 토큰 재발급");
    }
}
