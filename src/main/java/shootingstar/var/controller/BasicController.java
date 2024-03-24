package shootingstar.var.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shootingstar.var.Service.BasicService;
import shootingstar.var.dto.req.UserApplyVipDto;
import shootingstar.var.jwt.JwtTokenProvider;

@Tag(name = "BasicController", description = "Basic 사용자 사용가능 기능")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/basic")
public class BasicController {
    private static JwtTokenProvider jwtTokenProvider;
    private static BasicService basicService;
    @PostMapping("/applyVip")
    public ResponseEntity<String> applyVip(HttpServletRequest request,
                                           @Valid @RequestBody UserApplyVipDto userApplyVipDto){
            String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
            basicService.applyVip(userUUID,userApplyVipDto);
            return ResponseEntity.ok().body("vip apply success");
    }
}
