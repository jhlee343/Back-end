package shootingstar.var.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import shootingstar.var.dto.req.UserSignupReqDto;
import shootingstar.var.entity.User;

@Tag(name = "swagger 테스트 컨트롤러", description = "/swagger/TestController를 보면 됩니다.")
@RestController
public class TestController {

    @Operation(summary = "[예시] 사용자 로그인 API", description = "[예시] 사용자가 로그인할 때 사용하는 API")
    @GetMapping("/swagger/login")
    public String login() {
        return "hihi";
    }

    @Operation(summary = "[예시] 사용자 회원가입 API", description = "[예시] 사용자가 회원가입 할 때 사용하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "405", description = "Invalid input")
    })
    @PostMapping("/swagger/signup")
    public ResponseEntity<String> signup(@RequestBody UserSignupReqDto request) {

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
