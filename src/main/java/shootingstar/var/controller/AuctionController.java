package shootingstar.var.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shootingstar.var.Service.AuctionService;
import shootingstar.var.dto.req.AuctionCreateReqDto;
import shootingstar.var.jwt.JwtTokenProvider;

@Tag(name = "경매 컨트롤러", description= "vip 권한만 사용 가능합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vip/auction")
public class AuctionController {

    private final AuctionService auctionService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "경매 생성 API", description = "VIP 권한을 가진 사용자가 경매를 생성할 때 사용합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "경매 생성 성공")
    })
    @PostMapping("/create")
    public ResponseEntity<String> create(@Valid @RequestBody AuctionCreateReqDto reqDto, HttpServletRequest request) {
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        auctionService.create(reqDto, userUUID);
        return ResponseEntity.ok().body("경매 생성 성공");
    }

    @PatchMapping("/cancel/{auctionUUID}")
    public ResponseEntity<String> cancel(@PathVariable("auctionUUID") String auctionUUID, HttpServletRequest request) {
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        auctionService.cancel(auctionUUID, userUUID);
        return ResponseEntity.ok().body("경매 취소 성공");
    }
}
