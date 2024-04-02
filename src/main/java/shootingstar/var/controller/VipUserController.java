package shootingstar.var.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shootingstar.var.Service.VipUserService;
import shootingstar.var.dto.res.VipInfoDto;
import shootingstar.var.enums.type.AuctionType;
import shootingstar.var.jwt.JwtTokenProvider;

@Tag(name = "VipUserController", description = "VIP 유저 사용 컨트롤러")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vip")

public class VipUserController {

    private final JwtTokenProvider jwtTokenProvider;
    private final VipUserService vipService;

    @Operation(summary = "vip 소개 불러오기")
    @GetMapping("/info")
    public ResponseEntity<VipInfoDto> getVipInfo(HttpServletRequest request){
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        VipInfoDto vipInfo = vipService.getVipInfo(userUUID);
        return ResponseEntity.ok(vipInfo);

    }

    @Operation(summary = "경매 불러오기")
    @GetMapping("/auction/{auctionType}")
    public ResponseEntity<?> getVipAuctionList(@NotBlank @PathVariable("auctionType")AuctionType auctionType, HttpServletRequest request){
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        if(auctionType.equals(AuctionType.CANCEL)){

        }
        else if(auctionType.equals(AuctionType.PROGRESS)){

        }
        else if(auctionType.equals(AuctionType.SUCCESS)){
            
        }
        return null;
    }



}
