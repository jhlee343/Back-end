package shootingstar.var.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shootingstar.var.Service.ChatService;
import shootingstar.var.dto.req.ChatReportReqDto;
import shootingstar.var.dto.req.TicketReportReqDto;
import shootingstar.var.dto.res.DetailTicketResDto;
import shootingstar.var.dto.res.SaveChatMessageResDto;
import shootingstar.var.enums.type.UserType;
import shootingstar.var.exception.ErrorResponse;
import shootingstar.var.jwt.JwtTokenProvider;
@Tag(name = "채팅 컨트롤러", description = "basic, vip 권한 둘 다 사용 가능")
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final JwtTokenProvider jwtTokenProvider;

//    @Operation(summary = "채팅방UUID에 해당하는 채팅 메세지 전체 조회 API")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200",
//                    description = "- 사용자 타입이 VIP, BASIC일 때",
//                    content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SaveChatMessageResDto.class)))}),
//            @ApiResponse(responseCode = "404",
//                    description = "- 채팅방 정보 조회 실패 : 8200",
//                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
//            @ApiResponse(responseCode = "403",
//                    description =
//                                    "- 로그인한 사용자가 경매의 낙찰자도 주최자도 권한이 어드민도 아닐 때 : 8101\n" +
//                                    "- 채팅방이 닫혀 있는 경우 : 8100",
//                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
//    })
//    @GetMapping("/messageList/{chatRoomUUID}")
//    public ResponseEntity<List<SaveChatMessageResDto>> findMessageListByChatRoomUUID(@PathVariable("chatRoomUUID") String chatRoomUUID, HttpServletRequest request) {
//        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
//        List<SaveChatMessageResDto> messages = chatService.findMessageListByChatRoomUUID(chatRoomUUID, userUUID, null);
//        return ResponseEntity.ok().body(messages);
//    }

    @Operation(summary = "채팅방 신고하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "- 사용자 타입이 VIP, BASIC일 때"),
            @ApiResponse(responseCode = "400",
                    description =
                                    "- 잘못된 형식의 채팅방 UUID 입력 시 : 8001\n" +
                                    "- 잘못된 형식의 채팅방 신고 내용 입력 시 : 8002",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),

            @ApiResponse(responseCode = "404",
                    description = "- 채팅방 정보 조회 실패 : 8200",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403",
                    description = "- 로그인한 사용자가 경매의 낙찰자도 주최자도 아닐 경우 : 0101",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/report")
    public ResponseEntity<String> reportTicket(@Valid @RequestBody ChatReportReqDto reqDto, HttpServletRequest request) {
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        chatService.reportChat(reqDto, userUUID);
        return ResponseEntity.ok().body("식사권 신고 성공");
    }
}

