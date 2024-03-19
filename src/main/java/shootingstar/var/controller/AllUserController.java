package shootingstar.var.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shootingstar.var.Service.UserService;
import shootingstar.var.dto.req.UserSignupReqDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/all")
public class AllUserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody UserSignupReqDto reqDto) {

        userService.signup(reqDto);

        return ResponseEntity.ok().body("회원가입 성공");
    }

    @GetMapping("/duplicate/{nickname}")
    public ResponseEntity<Boolean> checkNicknameDuplicate(@PathVariable String nickname){
        return ResponseEntity.ok(userService.checkNicknameDuplicate(nickname));
    }
}
