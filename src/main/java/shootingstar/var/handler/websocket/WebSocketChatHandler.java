package shootingstar.var.handler.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import shootingstar.var.Service.ChatService;
import shootingstar.var.dto.req.ChatMessageReqDto;
import shootingstar.var.dto.res.SaveChatMessageResDto;
import shootingstar.var.exception.CustomException;
import shootingstar.var.jwt.JwtTokenProvider;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChatHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final ChatService chatService;

    private final Set<WebSocketSession> sessions = new HashSet<>();

    private final Map<String, Set<WebSocketSession>> chatRoomSessionMap = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("{} 연결됨", session.getId());
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        ChatMessageReqDto chatMessageDto = objectMapper.readValue(payload, ChatMessageReqDto.class);

        try {
            // 엑세스 토큰 검증
            validateAccessToken(chatMessageDto);

            // 채팅방 UUID에 해당하는 map에 session 추가
            String chatRoomUUID = chatMessageDto.getChatRoomUUID();
            addSessionToChatRoom(session, chatRoomUUID);

            // 채팅 메세지 저장
            SaveChatMessageResDto resDto = chatService.saveChatMessage(getUserUUID(chatMessageDto), chatMessageDto);

            // key가 chatRoomUUID인 session에 메세지 전송
            String messageJson = objectMapper.writeValueAsString(resDto);
            broadcastMessage(chatRoomUUID, messageJson);

        } catch (CustomException e) {
            String errorRes = objectMapper.writeValueAsString(e.getErrorCode());
            sendMessage(session, errorRes);

            session.close();
        }
    }

    private void validateAccessToken(ChatMessageReqDto chatMessageDto) {
        log.info("엑세스 토큰 검증");
        String accessToken = chatMessageDto.getAccessToken();
        jwtTokenProvider.validateAccessToken(accessToken);
    }

    private String getUserUUID(ChatMessageReqDto chatMessageDto) {
        Authentication authentication = jwtTokenProvider.getAuthenticationFromAccessToken(chatMessageDto.getAccessToken());
        return authentication.getName();
    }

    private void addSessionToChatRoom(WebSocketSession session, String chatRoomUUID) {
        if (!chatRoomSessionMap.containsKey(chatRoomUUID)) {
            chatRoomSessionMap.put(chatRoomUUID, new HashSet<>());
        }
        chatRoomSessionMap.get(chatRoomUUID).add(session);
    }

    private void broadcastMessage(String chatRoomUUID, String messageJson) throws IOException {
        Set<WebSocketSession> chatRoomSessions = chatRoomSessionMap.get(chatRoomUUID);
        if (chatRoomSessions != null) {
            for (WebSocketSession chatSession : chatRoomSessions) {
                if (chatSession.isOpen()) {
                    sendMessage(chatSession, messageJson);
                }
            }
        }
    }

    private void sendMessage(WebSocketSession session, String messageJson) throws IOException {
        session.sendMessage(new TextMessage(messageJson));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }
}
