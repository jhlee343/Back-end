package shootingstar.var.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shootingstar.var.Service.VipService;
import shootingstar.var.dto.res.VipInfoDto;
import shootingstar.var.entity.VipInfo;
import shootingstar.var.jwt.JwtTokenProvider;

@Tag(name = "VipUserController", description = "VIP 유저 사용 컨트롤러")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vip")

public class VipUserController {

    private final JwtTokenProvider jwtTokenProvider;
    private final VipService vipService;

    @Operation(summary = "vip 소개 불러오기")
    @GetMapping("/info")
    public ResponseEntity<VipInfoDto> getVipInfo(HttpServletRequest request){
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        VipInfoDto vipInfo = vipService.getVipInfo(userUUID);
        return ResponseEntity.ok(vipInfo);

    }

    @Operation(summary = "vip 소개 수정하기")
    @PatchMapping("/editInfo")
    public ResponseEntity<String> editInfo(HttpServletRequest request, @RequestBody VipInfoDto vipInfoDto){
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        vipService.editInfo(userUUID,vipInfoDto);
        return ResponseEntity.ok().body("edit Vip Info success");
    }

}
