package shootingstar.var.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shootingstar.var.Service.UserService;
import shootingstar.var.Service.dto.UserSignupReqDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody UserSignupReqDto reqDto) {

        userService.signup(reqDto);

        return ResponseEntity.ok().body("회원가입 성공");
    }

    @GetMapping("/test")
    public String test() {
        return "접근 성공";
    }
}
