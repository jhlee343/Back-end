package shootingstar.var.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shootingstar.var.Service.EmailService;
import shootingstar.var.Service.UserService;
import shootingstar.var.dto.req.CheckAuthCodeReqDto;
import shootingstar.var.dto.req.SendAuthCodeReqDto;
import shootingstar.var.dto.req.UserSignupReqDto;

@RestController
@RequiredArgsConstructor
@Validated
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
    public ResponseEntity<Boolean> checkNicknameDuplicate(@NotBlank @PathVariable String nickname){
        return ResponseEntity.ok(userService.checkNicknameDuplicate(nickname));
    }

    @PostMapping("/email/sendAuthCode")
    public ResponseEntity<String> sendAuthCode(@Valid @RequestBody SendAuthCodeReqDto reqDto) {
        emailService.sendAuthCodeEmail(reqDto.getEmail());
        return ResponseEntity.ok().body("인증코드를 발송하였습니다.");
    }

    @PostMapping("/email/checkAuthCode")
    public ResponseEntity<String> checkAuthCode(@Valid @RequestBody CheckAuthCodeReqDto reqDto) {
        emailService.validateCode(reqDto.getEmail(), reqDto.getCode());
        return ResponseEntity.ok().body("이메일 인증에 성공하였습니다.");
    }
}
