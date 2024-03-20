package shootingstar.var.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shootingstar.var.Service.UserService;
import shootingstar.var.dto.req.FollowingDto;
import shootingstar.var.Service.dto.UserProfileDto;
import shootingstar.var.dto.req.WarningListDto;
import shootingstar.var.entity.Warning;

import java.util.List;
import java.util.UUID;

@Validated
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
    public ResponseEntity<String> follow(@NotEmpty @PathVariable("followingId") UUID followingId, HttpServletRequest request) {
        userService.follow(followingId,request);
        return ResponseEntity.ok("follow success");
    }
    @DeleteMapping("/unfollow/{followUUID}")
    public ResponseEntity<String> unFollow(@PathVariable("followUUID") UUID followUUID) {
        userService.unFollow(followUUID);
        return ResponseEntity.ok().body("unfollow success");
    }

    @GetMapping("warningList")
    public ResponseEntity<WarningListDto> warningList(HttpServletRequest request){
        List<WarningListDto> warning = userService.findAllWarning(request);
        return null;
                //ResponseEntity.ok().body(warning);
    }


    @GetMapping("/test")
    public String test() {
        return "접근 성공";
    }

}
