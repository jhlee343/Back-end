package shootingstar.var.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "프로필 불러오기")
    @GetMapping("/profile/{nickname}")
    public ResponseEntity<UserProfileDto> getProfile(@PathVariable String nickname) {
        UserProfileDto profile = userService.getProfile(nickname);
        return ResponseEntity.ok(profile);
    }

    @Operation(summary = "VIP 확인")
    @GetMapping("/checkVIP")
    public ResponseEntity<Boolean> checkVIP(HttpServletRequest request) {
        return ResponseEntity.ok(userService.checkVIP(request));
    }

    @Operation(summary = "팔로우 리스트 불러오기")
    @GetMapping("/followingList")
    public ResponseEntity<?> followingList(HttpServletRequest request) {
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        List<FollowingDto> followingList = userService.findAllFollowing(userUUID);
        return ResponseEntity.ok().body(followingList);
    }
    @Operation(summary = "팔로우하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "follow success")
    })
    @GetMapping("/follow/{followingId}")
    public ResponseEntity<String> follow(@NotBlank @PathVariable("followingId") String followingId, HttpServletRequest request) {
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        userService.follow(followingId,userUUID);
        return ResponseEntity.ok("follow success");
    }
    @Operation(summary = "언팔로우")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "unfollow success")
    })
    @DeleteMapping("/unfollow/{followUUID}")
    public ResponseEntity<String> unFollow(@NotBlank @PathVariable("followUUID") String followUUID) {
        userService.unFollow(followUUID);
        return ResponseEntity.ok().body("unfollow success");
    }
    @Operation(summary = "경고 내역")
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
