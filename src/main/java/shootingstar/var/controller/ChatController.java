package shootingstar.var.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shootingstar.var.Service.ChatService;
import shootingstar.var.dto.res.SaveChatMessageResDto;
import shootingstar.var.jwt.JwtTokenProvider;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/messageList/{chatRoomUUID}")
    public ResponseEntity<List<SaveChatMessageResDto>> findMessageListByChatRoomUUID(@PathVariable("chatRoomUUID") String chatRoomUUID, HttpServletRequest request) {
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        List<SaveChatMessageResDto> messages = chatService.findMessageListByChatRoomUUID(chatRoomUUID, userUUID);
        return ResponseEntity.ok().body(messages);
    }
}

