package shootingstar.var.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shootingstar.var.Service.BidService;
import shootingstar.var.dto.res.BidInfoResDto;
import shootingstar.var.exception.ErrorResponse;

@Tag(name = "응찰 컨트롤러", description = "basic, vip 권한 둘 다 사용 가능")
@RestController
@RequestMapping("/api/bid")
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;

    @Operation(summary = "경매 UUID에 해당하는 상위 10개의 응찰 기록 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "- 사용자 타입이 VIP, BASIC일 때",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BidInfoResDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "- 경매 정보 조회 실패 : 2200",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "409",
                    description = "- 경매가 진행중이지 않을 때 : 2300",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/{auctionUUID}")
    public ResponseEntity<BidInfoResDto> findBidInfo(@PathVariable("auctionUUID") String auctionUUID) {
        BidInfoResDto resDto = bidService.findBidInfo(auctionUUID);
        return ResponseEntity.ok().body(resDto);
    }
}
