package shootingstar.var.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shootingstar.var.Service.BasicUserService;
import shootingstar.var.dto.req.UserApplyVipDto;
import shootingstar.var.dto.res.TicketListResDto;
import shootingstar.var.dto.res.UserAuctionParticipateResDto;
import shootingstar.var.dto.res.UserAuctionSuccessResDto;
import shootingstar.var.enums.type.TicketSortType;
import shootingstar.var.jwt.JwtTokenProvider;

@Tag(name = "BasicController", description = "Basic 사용자 사용가능 기능")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/basic")
public class BasicUserController {
    private final JwtTokenProvider jwtTokenProvider;
    private final BasicUserService basicUserService;

    @Operation(summary = "vip 신청")
    @PostMapping("/applyVip")
    public ResponseEntity<String> applyVip(HttpServletRequest request,
                                           @Valid @RequestBody UserApplyVipDto userApplyVipDto){
            String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
            basicUserService.applyVip(userUUID,userApplyVipDto);
            return ResponseEntity.ok().body("vip apply success");
    }

    @Operation(summary = "vip 신청 상태 조회")
    @GetMapping("/applyCheck")
    public ResponseEntity<String> applyCheck(HttpServletRequest request){
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        String userApplyType = basicUserService.applyCheck(userUUID);
        return ResponseEntity.ok(userApplyType);
    }
    @Operation(summary = "사용자 식사권 리스트 불러오기")
    @GetMapping("/ticketList")
    public ResponseEntity<?> getTicketList(HttpServletRequest request,
                                           @RequestParam(value = "ticketSortType", required = false, defaultValue = "TIME_DESC") TicketSortType ticketSortType,
                                           @RequestParam(value = "search", required = false) String search,
                                           @PageableDefault(size =10) Pageable pageable){
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        Page<TicketListResDto> findAllTicketListDto = basicUserService.getAllTicketList(userUUID, ticketSortType,search, pageable);
        return ResponseEntity.ok(findAllTicketListDto);
    }
    @Operation(summary = "참여중인 경매 불러오기")
    @GetMapping("/auction/participate")
    public ResponseEntity<?> getParticipateAuctionList(HttpServletRequest request,@PageableDefault(size = 10) Pageable pageable){
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        Page<UserAuctionParticipateResDto> userAuctionParticipateLists = basicUserService.participateAuctionList(userUUID,pageable);
        return ResponseEntity.ok(userAuctionParticipateLists);
    }

    @Operation(summary = "낙찰받은 만남 전 경매 불러오기")
    @GetMapping("/auction/successBefore")
    public ResponseEntity<?> getSuccessBeforeAuctionList(HttpServletRequest request, @PageableDefault(size = 10) Pageable pageable){
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        Page<UserAuctionSuccessResDto> userAuctionSuccessLists = basicUserService.successBeforeAuctionList(userUUID,pageable);
        return ResponseEntity.ok(userAuctionSuccessLists);
    }
    @Operation(summary = "낙찰받은 만남 후 경매 불러오기")
    @GetMapping("/auction/successAfter")
    public ResponseEntity<?> getSuccessAfterAuctionList(HttpServletRequest request, @PageableDefault(size = 10) Pageable pageable){
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        Page<UserAuctionSuccessResDto> userAuctionSuccessLists = basicUserService.successAfterAuctionList(userUUID,pageable);
        return ResponseEntity.ok(userAuctionSuccessLists);
    }
}
