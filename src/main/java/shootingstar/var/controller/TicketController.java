package shootingstar.var.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shootingstar.var.Service.TicketService;
import shootingstar.var.dto.res.DetailTicketResDto;
import shootingstar.var.exception.ErrorResponse;
import shootingstar.var.jwt.JwtTokenProvider;

@Tag(name = "식사권 컨트롤러", description= "식사권 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ticket")
public class TicketController {

    private final TicketService ticketService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "식사권 상세 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "- 사용자 타입이 VIP, BASIC일 때",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DetailTicketResDto.class))}),
            @ApiResponse(responseCode = "404",
                    description =
                                    "- 식사권 정보 조회 실패 : 6200\n" +
                                    "- 낙찰자 정보 조회 실패 : 1201\n",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403",
                    description = "- 로그인한 사용자가 경매의 낙찰자도 주최자도 아닐 때 : 0101",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/{ticketUUID}")
    public ResponseEntity<DetailTicketResDto> findByTicketUUID(@PathVariable("ticketUUID") String ticketUUID, HttpServletRequest request) {
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        DetailTicketResDto detailTicketResDto = ticketService.detailTicket(ticketUUID, userUUID);
        return ResponseEntity.ok().body(detailTicketResDto);
    }
}
