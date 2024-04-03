package shootingstar.var.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shootingstar.var.Service.VipUserService;
import shootingstar.var.dto.res.UserAuctionInvalidityResDto;
import shootingstar.var.dto.res.UserAuctionParticipateResDto;
import shootingstar.var.dto.res.UserAuctionSuccessResDto;
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
    public ResponseEntity<VipInfoDto> getVipInfo(HttpServletRequest request) {
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        VipInfoDto vipInfo = vipService.getVipInfo(userUUID);
        return ResponseEntity.ok(vipInfo);

    }

    @Operation(summary = "경매 불러오기")
    @GetMapping("/auction/{auctionType}")
    public ResponseEntity<?> getVipAuctionList(@NotBlank @PathVariable("auctionType") AuctionType auctionType,
                                               HttpServletRequest request, @PageableDefault(size = 10) Pageable pageable) {
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);

        if (auctionType.equals(AuctionType.PROGRESS)) {
            //진행중
            Page<UserAuctionParticipateResDto> userAuctionParticipateLists = vipService.getVipUserAuctionProgress(userUUID, pageable);
            return ResponseEntity.ok(userAuctionParticipateLists);
        } else if (auctionType.equals(AuctionType.SUCCESS)) {
            //성공
            Page<UserAuctionSuccessResDto> userAuctionSuccessLists = vipService.getVipUserAuctionSuccess(userUUID, pageable);
            return ResponseEntity.ok(userAuctionSuccessLists);
        }
        else {
            //유찰
            Page<UserAuctionInvalidityResDto> userAuctionInvalidityLists = vipService.getVipUserAuctionInvalidity(userUUID,pageable);
            return ResponseEntity.ok(userAuctionInvalidityLists);
        }
    }

}
