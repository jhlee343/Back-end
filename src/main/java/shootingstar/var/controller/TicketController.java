package shootingstar.var.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shootingstar.var.Service.TicketService;
import shootingstar.var.dto.req.MeetingTimeSaveReqDto;
import shootingstar.var.dto.req.ReviewSaveReqDto;
import shootingstar.var.dto.req.TicketReportReqDto;
import shootingstar.var.dto.res.DetailTicketResDto;
import shootingstar.var.dto.res.MeetingTimeResDto;
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

    @Operation(summary = "식사권 만남 시작 버튼 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "- 사용자 타입이 VIP, BASIC일 때 : 만남 시작 시간 저장 성공"),
            @ApiResponse(responseCode = "400",
                    description =
                                    "- 잘못된 형식의 식사권 UUID 입력 시 : 6001\n" +
                                    "- 잘못된 형식의 만남 시작 시간 입력 시 : 6002\n",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404",
                    description =
                                    "- 식사권 정보 조회 실패 : 6200\n" +
                                    "- 사용자 정보 조회 실패 : 1201\n",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403",
                    description = "- 로그인한 사용자가 식사권의 낙찰자도 주최자도 아닐 때 : 0101",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "409",
                    description = "- 로그인한 사용자가 이미 만남 시작 버튼을 눌렀을 경우 : 6300",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/checkTime")
    public ResponseEntity<String> saveMeetingTime(@Valid @RequestBody MeetingTimeSaveReqDto reqDto, HttpServletRequest request) {
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        ticketService.saveMeetingTime(reqDto, userUUID);
        return ResponseEntity.ok().body("만남 시작 시간 저장 성공");
    }

    @Operation(summary = "식사권 만남 시작 시간 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "- 사용자 타입이 VIP, BASIC일 때",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MeetingTimeResDto.class))}),
            @ApiResponse(responseCode = "404",
                    description =
                                    "- 식사권 정보 조회 실패 : 6200\n" +
                                    "- 낙찰자와 주최자 중 한명이라도 만남 시작 버튼을 누르지 않았을 경우 : 6201\n",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403",
                    description = "- 로그인한 사용자가 식사권의 낙찰자도 주최자도 아닐 때 : 0101",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/checkTime/{ticketUUID}")
    public ResponseEntity<MeetingTimeResDto> findMeetingTimeByTicketUUID(@PathVariable("ticketUUID") String ticketUUID, HttpServletRequest request) {
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        LocalDateTime startMeetingTime = ticketService.findMeetingTimeByTicketUUID(ticketUUID, userUUID);
        return ResponseEntity.ok().body(new MeetingTimeResDto(startMeetingTime));
    }

    @Operation(summary = "식사권 신고하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "- 사용자 타입이 VIP, BASIC일 때 : 식사권 신고 성공"),
            @ApiResponse(responseCode = "400",
                    description =
                                    "- 잘못된 형식의 식사권 UUID 입력 시 : 6001\n" +
                                    "- 잘못된 형식의 식사권 신고 내용 입력 시 : 6003\n" +
                                    "- 잘못된 형식의 식사권 신고 증거 URL 입력 시 : 6004\n",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404",
                    description = "- 식사권 정보 조회 실패 : 6200",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403",
                    description = "- 로그인한 사용자가 식사권의 낙찰자도 주최자도 아닐 때 : 0101",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "409",
                    description = "- 이미 같은 식사권에 신고한 적이 있는 경우 : 6301",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/report")
    public ResponseEntity<String> reportTicket(@Valid @RequestBody TicketReportReqDto reqDto, HttpServletRequest request) {
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        ticketService.reportTicket(reqDto, userUUID);
        return ResponseEntity.ok().body("식사권 신고 성공");
    }

    @Operation(summary = "식사권 취소하는 API", description = "낙찰자와 주최자 둘 다 사용")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "- 식사권의 낙찰자이거나 주최자일 때 : 식사권 취소 성공"),
            @ApiResponse(responseCode = "404",
                    description =
                                    "- 식사권 정보 조회 실패 : 6200\n" +
                                    "- 사용자 정보 조회 실패 : 1201",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403",
                    description = "- 로그인한 사용자가 경매의 낙찰자도 주최자도 아닐 때 : 0101",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "409",
                    description =
                                    "- 식사 시간 이후에 식사권 취소를 요청하는 경우 : 6302\n" +
                                    "- 이미 취소된 식사권에 같은 요청을 보낼 경우 : 6303",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PatchMapping("/cancel/{ticketUUID}")
    public ResponseEntity<String> cancelTicket(@PathVariable("ticketUUID") String ticketUUID, HttpServletRequest request) {
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        ticketService.cancelTicket(ticketUUID, userUUID);
        return ResponseEntity.ok().body("식사권 취소 성공");
    }

    @Operation(summary = "식사권 리뷰 작성 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "식사권 리뷰 작성 성공"),
            @ApiResponse(responseCode = "400",
                    description =
                                    "- 잘못된 형식의 식사권 UUID 입력 시 : 6001\n" +
                                    "- 잘못된 형식의 리뷰 내용 입력 시 : 6005\n" +
                                    "- 잘못된 형식의 리뷰 점수 입력 시 : 6006\n" +
                                    "- 만남 시간 + 2시간 전에 리뷰 작성 시 : 6007",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404",
                    description = "- 식사권 정보 조회 실패 : 6200",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "409",
                    description = "- 해당 식사권에 대한 리뷰를 작성한 적이 있을 때 : 6304",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403",
                    description =
                                    "- 로그인한 사용자가 식사권의 낙찰자도 주최자도 아닐 때 : 0101\n" +
                                    "- 식사권이 닫힌 경우 : 6100",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/review")
    public ResponseEntity<String> saveReview(@Valid @RequestBody ReviewSaveReqDto reqDto, HttpServletRequest request) {
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        ticketService.saveReview(reqDto, userUUID);
        return ResponseEntity.ok().body("식사권 리뷰 작성 성공");
    }
}
