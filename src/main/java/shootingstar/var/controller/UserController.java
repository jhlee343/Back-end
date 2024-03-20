package shootingstar.var.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shootingstar.var.Service.UserService;
import shootingstar.var.dto.req.FollowingDto;
import shootingstar.var.dto.req.UserProfileDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

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
        List<FollowingDto> followingList = userService.findAllFollowing(request);
        return ResponseEntity.ok().body(followingList);
    }
    @GetMapping("/follow/{followingId}")
    public ResponseEntity<String> follow(@PathVariable("followingId") String followingId, HttpServletRequest request) {
        userService.follow(followingId,request);
        return ResponseEntity.ok("follow success");
    }
    @DeleteMapping("/unfollow/{followUUID}")
    public ResponseEntity<String> unFollow(@PathVariable("followUUID") String followUUID) {
        userService.unFollow(followUUID);
        return ResponseEntity.ok().body("unfollow success");
    }


    @GetMapping("/test")
    public String test() {
        return "접근 성공";
    }

}
