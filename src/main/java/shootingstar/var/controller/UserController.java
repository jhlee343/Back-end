package shootingstar.var.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shootingstar.var.Service.UserService;
import shootingstar.var.dto.req.FollowingDto;
import shootingstar.var.dto.req.UserProfileDto;
import shootingstar.var.dto.req.WarningListDto;
import shootingstar.var.jwt.JwtTokenProvider;

import java.util.List;

@Tag(name = "UserController", description = "로그인 후 사용 가능 컨트롤러")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/profile/{nickname}")
    public ResponseEntity<UserProfileDto> getProfile(@PathVariable String nickname) {
        UserProfileDto profile = userService.getProfile(nickname);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/checkVIP")
    public ResponseEntity<Boolean> checkVIP(HttpServletRequest request) {
        return ResponseEntity.ok(userService.checkVIP(request));
    }

    @GetMapping("/followingList")
    public ResponseEntity<?> followingList(HttpServletRequest request) {
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        List<FollowingDto> followingList = userService.findAllFollowing(userUUID);
        return ResponseEntity.ok().body(followingList);
    }
    @GetMapping("/follow/{followingId}")
    public ResponseEntity<String> follow(@NotBlank @PathVariable("followingId") String followingId, HttpServletRequest request) {
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        userService.follow(followingId,userUUID);
        return ResponseEntity.ok("follow success");
    }
    @DeleteMapping("/unfollow/{followUUID}")
    public ResponseEntity<String> unFollow(@NotBlank @PathVariable("followUUID") String followUUID) {
        userService.unFollow(followUUID);
        return ResponseEntity.ok().body("unfollow success");
    }

    @GetMapping("warningList")
    public ResponseEntity<WarningListDto> warningList(HttpServletRequest request){
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        List<WarningListDto> warning = userService.findAllWarning(userUUID);
        return null;
                //ResponseEntity.ok().body(warning);
    }


    @GetMapping("/test")
    public String test() {
        return "접근 성공";
    }

}
