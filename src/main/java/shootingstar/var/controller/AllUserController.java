package shootingstar.var.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shootingstar.var.Service.EmailService;
import shootingstar.var.Service.UserService;
import shootingstar.var.Service.dto.UserSignupReqDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/all")
public class AllUserController {
    private final UserService userService;
    private final EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody UserSignupReqDto reqDto) {

        userService.signup(reqDto);

        return ResponseEntity.ok().body("회원가입 성공");
    }

    @GetMapping("/duplicate/{nickname}")
    public ResponseEntity<Boolean> checkNicknameDuplicate(@PathVariable String nickname){
        return ResponseEntity.ok(userService.checkNicknameDuplicate(nickname));
    }

    @PostMapping("/email/sendAuthCode")
    public ResponseEntity<String> sendAuthCode() {
//        emailService.sendAuthCodeEmail();
        return ResponseEntity.ok().body("인증코드를 발송하였습니다.");
    }

    @PostMapping("/email/checkAuthCode")
    public ResponseEntity<String> checkAuthCode() {
//        emailService.validateCode();
        return ResponseEntity.ok().body("인증코드를 발송하였습니다.");
    }
}
