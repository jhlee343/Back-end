package shootingstar.var.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shootingstar.var.Service.UserService;
import shootingstar.var.Service.dto.FollowingDto;
import shootingstar.var.Service.dto.UserProfileDto;
import shootingstar.var.Service.dto.UserSignupReqDto;
import shootingstar.var.repository.FollowRepository;

import java.util.List;

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

    @GetMapping("/duplicate/{nickname}")
    public ResponseEntity<Boolean> checkNicknameDuplicate(@PathVariable String nickname){
        return ResponseEntity.ok(userService.checkNicknameDuplicate(nickname));
    }

    @GetMapping("/profile/{nickname}")
    public ResponseEntity<UserProfileDto> getProfile(@PathVariable String nickname){
        UserProfileDto profile = userService.getProfile(nickname);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/checkVIP/{nickname}")
    public ResponseEntity<Boolean> checkVIP(@PathVariable String nickname){
        return ResponseEntity.ok(userService.checkVIP(nickname));
    }

    @GetMapping("/followingList")
    public ResponseEntity<?> followingList(@RequestParam("nickname") String nickname){
        List<FollowingDto> followingList = userService.findAllFollowing(nickname);
        return ResponseEntity.ok().body(followingList);
    }

    @DeleteMapping("/unfollow/{followingId}")
    public ResponseEntity<String> unFollow(@PathVariable("followingId") Long followingId){

    return ResponseEntity.ok().body("unfollow success");
    }
    @GetMapping("/test")
    public String test() {
        return "접근 성공";
    }
}
