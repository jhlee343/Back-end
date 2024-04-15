package shootingstar.var.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shootingstar.var.Service.AdminService;
import shootingstar.var.Service.AuctionService;
import shootingstar.var.Service.ChatService;
import shootingstar.var.dto.req.AdminLoginReqDto;
import shootingstar.var.dto.req.AdminSignupReqDto;
import shootingstar.var.dto.req.BannerReqDto;
import shootingstar.var.dto.res.*;
import shootingstar.var.enums.type.UserType;
import shootingstar.var.exception.ErrorResponse;
import shootingstar.var.jwt.TokenInfo;
import shootingstar.var.jwt.TokenProperty;
import shootingstar.var.util.TokenUtil;

import java.util.List;

@Tag(name = "AdminController", description = "관리자 컨트롤러")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lookAtMe")
public class AdminController {
    private final AdminService adminService;
    private final TokenProperty tokenProperty;
    private final AuctionService auctionService;
    private final ChatService chatService;

    @Operation(summary = "관리자 회원가입 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리자 회원가입 성공", content = {
                    @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400",
                    description =
                                    "- 잘못된 형식의 관리자 ID : 9001\n" +
                                    "- 잘못된 형식의 관리자 비밀번호 : 9002\n" +
                                    "- 잘못된 형식의 관리자 닉네임 : 9003\n" +
                                    "- 잘못된 형식의 관리자 비밀 키 : 9004",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "409",
                    description =
                                    "- 사용할 수 없는 관리자 ID : 9301\n" +
                                    "- 사용할 수 없는 관리자 닉네임 : 9302\n" +
                                    "- 잘못된 값의 관리자 비밀 키 : 9303",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody AdminSignupReqDto reqDto) {
        adminService.signup(reqDto.getAdminLoginId(), reqDto.getAdminPassword(), reqDto.getAdminNickname(), reqDto.getAdminSecretKey());
        return ResponseEntity.ok().body("회원가입에 성공하였습니다.");
    }

    @Operation(summary = "관리자 로그인 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리자 로그인 성공", content = {
                    @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400",
                    description =
                                    "- 잘못된 형식의 관리자 ID : 9001\n" +
                                    "- 잘못된 형식의 관리자 비밀번호 : 9002",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403",
                    description = "로그인 실패 : 잘못된 관리자 아이디 혹은 패스워드",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody AdminLoginReqDto reqDto, HttpServletResponse response) {
        TokenInfo tokenInfo = adminService.login(reqDto.getAdminLoginId(), reqDto.getAdminPassword());
        TokenUtil.addHeader(response, tokenInfo.getAccessToken());
        TokenUtil.updateCookie(response, tokenInfo.getRefreshToken(), tokenProperty.getREFRESH_EXPIRE());
        return ResponseEntity.ok().body("로그인에 성공하였습니다.");
    }

    @GetMapping("/test")
    public String test() {
        return "관리자 접근 성공";
    }

    @Operation(summary = "회원 리스트 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "회원 리스트 조회 성공",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AllUsersDto.class))}),
    })
    @GetMapping("/userList")
    public ResponseEntity<Page<AllUsersDto>> getUserList(
            @RequestParam(value = "search", required = false, defaultValue = "") String search,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<AllUsersDto> userList = adminService.getAllUsers(search, pageable);
        return ResponseEntity.ok().body(userList);
    }

    @Operation(summary = "회원 경고 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "회원 경고 성공",
                    content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 형식의 사용자 고유번호 : 1008",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404",
                    description = "존재하지 않는 사용자 : 1201",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "409",
                    description = "이미 정지된 사용자 : 9304",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PatchMapping("/warning/{userUUID}")
    public ResponseEntity<String> warning(@NotBlank @PathVariable String userUUID) {
        adminService.warning(userUUID);
        return ResponseEntity.ok().body("해당 회원이 경고되었습니다.");
    }

    @Operation(summary = "VIP 신청 리스트 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "VIP 신청 리스트 조회 성공",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AllVipInfosDto.class))}),

    })
    @GetMapping("/vip/requestList")
    public ResponseEntity<Page<AllVipInfosDto>> getVipInfoList(
            @RequestParam(value = "search", required = false, defaultValue = "") String search,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<AllVipInfosDto> vipInfoList = adminService.getAllVipInfos(search, pageable);
        return ResponseEntity.ok().body(vipInfoList);
    }

    @Operation(summary = "VIP 신청 상세정보 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "VIP 신청 리스트 조회 성공",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AllVipInfosDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 형식의 VIP 정보 고유번호 : 7001",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404",
                    description = "존재하지 않는 VIP 정보 : 7200",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/vip/requestDetail/{vipInfoUUID}")
    public ResponseEntity<AllVipInfosDto> getVipInfoDetail(@NotBlank @PathVariable("vipInfoUUID") String vipInfoUUID) {
        AllVipInfosDto vipInfoDetail = adminService.getVipInfoDetail(vipInfoUUID);
        return ResponseEntity.ok().body(vipInfoDetail);
    }

    @Operation(summary = "VIP 신청 승인 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "VIP 신청 승인 성공",
                    content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 형식의 VIP 정보 고유번호 : 7001",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404",
                    description = "존재하지 않는 VIP 정보 : 7200",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "409",
                    description = "이미 승인 또는 반려된 VIP 정보 : 7301",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PatchMapping("/vip/approve/{vipInfoUUID}")
    public ResponseEntity<String> approveVipInfo(@NotBlank @PathVariable("vipInfoUUID") String vipInfoUUID) {
        adminService.approveVipInfo(vipInfoUUID);
        return ResponseEntity.ok().body("VIP 신청 승인");
    }

    @Operation(summary = "VIP 신청 반려 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "VIP 신청 반려 성공",
                    content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 형식의 VIP 정보 고유번호 : 7001",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404",
                    description = "존재하지 않는 VIP 정보 : 7200",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "409",
                    description = "이미 승인 또는 반려된 VIP 정보 : 7301",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PatchMapping("/vip/refusal/{vipInfoUUID}")
    public ResponseEntity<String> refusalVip(@NotBlank @PathVariable("vipInfoUUID") String vipInfoUUID) {
        adminService.refusalVipInfo(vipInfoUUID);
        return ResponseEntity.ok().body("VIP 신청 반려");
    }

    @Operation(summary = "경매 취소 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "경매 취소 성공",
                    content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 형식의 경매 고유번호 : 2006",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404",
                    description = "존재하지 않는 경매 : 2200",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "409",
                    description = "진행 중이 아닌 경매 : 2300",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PatchMapping("/auction/cancel/{auctionUUID}")
    public ResponseEntity<String> cancelAuction(@NotBlank @PathVariable String auctionUUID) {
        auctionService.cancel(auctionUUID, null, UserType.ROLE_ADMIN.toString());
        return ResponseEntity.ok().body("경매가 취소되었습니다.");
    }

    @Operation(summary = "식사권 리스트 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "식사권 리스트 조회 성공",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AllTicketsDto.class))}),

    })
    @GetMapping("/ticketList")
    public ResponseEntity<Page<AllTicketsDto>> getTicketList(
            @RequestParam(value = "search", required = false, defaultValue = "") String search,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<AllTicketsDto> ticketList = adminService.getAllTickets(search, pageable);
        return ResponseEntity.ok().body(ticketList);
    }

    @Operation(summary = "식사권 상태 변경 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "식사권 상태 변경 성공",
                    content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 형식의 식사권 고유번호 : 6001",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404",
                    description = "존재하지 않는 식사권 : 6200",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "409",
                    description = "이미 닫힌 식사권 : 6303",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PatchMapping("/ticket/changeState/{ticketUUID}")
    public ResponseEntity<String> changeTicketState(@NotBlank @PathVariable String ticketUUID) {
        adminService.changeTicket(ticketUUID);
        return ResponseEntity.ok().body("식사권이 닫혔습니다.");
    }

    @Operation(summary = "채팅방 리스트 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "채팅방 리스트 조회 성공",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AllChatRoomsDto.class))}),

    })
    @GetMapping("/chat/roomList")
    public ResponseEntity<Page<AllChatRoomsDto>> getChatRoomList(
            @RequestParam(value = "search", required = false, defaultValue = "") String search,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<AllChatRoomsDto> chatRoomList = adminService.getAllChatRooms(search, pageable);
        return ResponseEntity.ok().body(chatRoomList);
    }

    @Operation(summary = "채팅 내역 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "채팅 내역 조회 성공",
                    content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SaveChatMessageResDto.class)))}),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 형식의 채팅방 고유번호 : 8001",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404",
                    description = "존재하지 않는 채팅방 : 8200",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("chat/history/{chatRoomUUID}")
    public ResponseEntity<List<SaveChatMessageResDto>> findMessageListByChatRoomUUID(@NotBlank @PathVariable("chatRoomUUID") String chatRoomUUID) {
        List<SaveChatMessageResDto> messages = chatService.findMessageListByChatRoomUUID(chatRoomUUID, null, UserType.ROLE_ADMIN.toString());
        return ResponseEntity.ok().body(messages);
    }

    @Operation(summary = "채팅방 상태 변경 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "채팅방 상태 변경 성공",
                    content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 형식의 채팅방 고유번호 : 8001",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404",
                    description = "존재하지 않는 채팅방 : 8200",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "409",
                    description = "이미 닫힌 채팅방 : 8100",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PatchMapping("/chat/changeState/{chatRoomUUID}")
    public ResponseEntity<String> changeChatState(@NotBlank @PathVariable String chatRoomUUID) {
        adminService.changeChat(chatRoomUUID);
        return ResponseEntity.ok().body("채팅방이 닫혔습니다.");
    }

    @Operation(summary = "리뷰 리스트 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "리뷰 리스트 조회 성공",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AllReviewsDto.class))}),

    })
    @GetMapping("/reviewList")
    public ResponseEntity<Page<AllReviewsDto>> getReviewList(
            @RequestParam(value = "search", required = false, defaultValue = "") String search,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<AllReviewsDto> reviewList = adminService.getAllReviews(search, pageable);
        return ResponseEntity.ok().body(reviewList);
    }

    @Operation(summary = "리뷰 상태 변경 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "리뷰 상태 변경 성공",
                    content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 형식의 리뷰 고유번호 : 5001",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404",
                    description = "존재하지 않는 리뷰 : 5201",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "409",
                    description = "이미 숨겨진 리뷰 : 5300",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PatchMapping("/review/changeState/{reviewUUID}")
    public ResponseEntity<String> changeReviewState(@NotBlank @PathVariable String reviewUUID) {
        adminService.changeReview(reviewUUID);
        return ResponseEntity.ok().body("이제 리뷰가 보이지 않습니다.");
    }

    @Operation(summary = "환전 신청서 리스트 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "환전 신청서 리스트 조회 성공",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AllExchangesDto.class))}),

    })
    @GetMapping("/exchange/requestList")
    public ResponseEntity<Page<AllExchangesDto>> getExchangeList(
            @RequestParam(value = "search", required = false, defaultValue = "") String search,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<AllExchangesDto> exchangeList = adminService.getAllExchanges(search, pageable);
        return ResponseEntity.ok().body(exchangeList);
    }

    @Operation(summary = "환전 신청 승인 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "환전 신청 승인 성공",
                    content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 형식의 환전 신청서 고유번호 : 3003",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404",
                    description = "존재하지 않는 환전 신청서 : 3201",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "409",
                    description = "이미 승인 또는 반려된 환전 신청서 : 3300",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PatchMapping("/exchange/approve/{exchangeUUID}")
    public ResponseEntity<String> approveExchange(@NotBlank @PathVariable String exchangeUUID) {
        adminService.approveExchange(exchangeUUID);
        return ResponseEntity.ok().body("환전 신청이 승인되었습니다.");
    }

    @Operation(summary = "환전 신청 반려 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "환전 신청 승인 성공",
                    content = {@Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 형식의 환전 신청서 고유번호 : 3003",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404",
                    description = "존재하지 않는 환전 신청서 : 3201",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "409",
                    description = "이미 승인 또는 반려된 환전 신청서 : 3300",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PatchMapping("/exchange/refusal/{exchangeUUID}")
    public ResponseEntity<String> refusalExchange(@NotBlank @PathVariable String exchangeUUID) {
        adminService.refusalExchange(exchangeUUID);
        return ResponseEntity.ok().body("환전 신청이 반려되었습니다.");
    }

    @Operation(summary = "배너 추가 API")
    @PostMapping("/banner/add")
    public ResponseEntity<String> add(@Valid @RequestBody BannerReqDto reqDto) {
        adminService.addBanner(reqDto.getBannerImgUrl(), reqDto.getTargetUrl());
        return ResponseEntity.ok().body("배너가 추가되었습니다.");
    }

    @Operation(summary = "배너 연결 URL 수정 API")
    @PatchMapping("/banner/edit/{bannerUUID}")
    public ResponseEntity<String> edit(
            @NotBlank @PathVariable String bannerUUID,
            @RequestParam String targetUrl) {
        adminService.editBanner(bannerUUID, targetUrl);
        return ResponseEntity.ok().body("배너 연결 URL이 수정되었습니다.");
    }

    @Operation(summary = "배너 삭제 API")
    @DeleteMapping("/banner/delete/{bannerUUID}")
    public ResponseEntity<String> delete(@NotBlank @PathVariable String bannerUUID) {
        adminService.deleteBanner(bannerUUID);
        return ResponseEntity.ok().body("배너가 삭제되었습니다.");
    }


}
