package shootingstar.var.Service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shootingstar.var.dto.req.ChatMessageReqDto;
import shootingstar.var.dto.req.ChatReportReqDto;
import shootingstar.var.dto.res.SaveChatMessageResDto;
import shootingstar.var.entity.User;
import shootingstar.var.entity.chat.ChatMessage;
import shootingstar.var.entity.chat.ChatReport;
import shootingstar.var.entity.chat.ChatRoom;
import shootingstar.var.entity.ticket.Ticket;
import shootingstar.var.enums.type.UserType;
import shootingstar.var.exception.CustomException;
import shootingstar.var.exception.ErrorCode;
import shootingstar.var.repository.chat.ChatMessageRepository;
import shootingstar.var.repository.chat.ChatReportRepository;
import shootingstar.var.repository.chat.ChatRoomRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatReportRepository chatReportRepository;

    @Transactional
    public SaveChatMessageResDto saveChatMessage(String userUUID, ChatMessageReqDto messageDto) {
        // 채팅방 존재 여부
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomUUID(messageDto.getChatRoomUUID())
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        log.info("채팅방 존재함");

        // 로그인 한 사용자가 식사권의 낙찰자도 주최자도 아닐 경우
        checkUserAccessToTicket(userUUID, chatRoom);

        // 채팅방이 닫혀 있는지 확인
        isChatRoomOpen(chatRoom);

        String nickname = getNickname(userUUID, chatRoom);
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .senderNickname(nickname)
                .chatContent(messageDto.getMessage())
                .build();

        ChatMessage findChatMessage = chatMessageRepository.save(chatMessage);

        return SaveChatMessageResDto.builder()
                .nickname(findChatMessage.getSenderNickname())
                .content(findChatMessage.getChatContent())
                .sendTime(findChatMessage.getCreatedTime())
                .build();
    }

    private String getNickname(String userUUID, ChatRoom chatRoom) {
        String nickname = "";
        if (chatRoom.getTicket().getOrganizer().getUserUUID().equals(userUUID)) {
            nickname = chatRoom.getTicket().getOrganizer().getNickname();
        } else {
            nickname = chatRoom.getTicket().getWinner().getNickname();
        }
        return nickname;
    }

    @Transactional
    public List<SaveChatMessageResDto> findMessageListByChatRoomUUID(String chatRoomUUID, String userUUID, String userType) {
        // 채팅방 존재 여부
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomUUID(chatRoomUUID)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        log.info("채팅방 존재함");

        // 로그인 한 사용자가 식사권의 낙찰자도 주최자도 권한이 어드민도 아닐 경우
        checkChatMessageAccess(userUUID, chatRoom, userType);

        // 채팅방이 닫혀 있는지 확인
        isChatRoomOpen(chatRoom);

        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomId(chatRoom.getChatRoomId());
        return chatMessages.stream()
                .map(message -> SaveChatMessageResDto.builder()
                        .nickname(message.getSenderNickname())
                        .content(message.getChatContent())
                        .sendTime(message.getCreatedTime())
                        .build())
                .collect(Collectors.toList());
    }

    private static void isChatRoomOpen(ChatRoom chatRoom) {
        if (!chatRoom.isChatRoomIsOpened()) {
            throw new CustomException(ErrorCode.CHAT_ROOM_ACCESS_DENIED);
        }
    }

    private void checkChatMessageAccess(String userUUID, ChatRoom chatRoom, String userType) {
        if (!chatRoom.getTicket().getOrganizer().getUserUUID().equals(userUUID)
                && !chatRoom.getTicket().getWinner().getUserUUID().equals(userUUID)
                && !userType.equals(UserType.ROLE_ADMIN.toString())) {
            throw new CustomException(ErrorCode.CHAT_MESSAGE_ACCESS_DENIED);
        }
    }

    @Transactional
    public void reportChat(ChatReportReqDto reqDto, String userUUID) {
        // 채팅방 존재 여부
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomUUID(reqDto.getChatRoomUUID())
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        // 로그인 한 사용자가 식사권의 낙찰자도 주최자도 아닐 경우
        checkUserAccessToTicket(userUUID, chatRoom);

        // 신고자 닉네임과 신고 당한 닉네임 할당
        NicknamePair nicknamePair = assignNicknames(userUUID, chatRoom);

        ChatReport chatReport = ChatReport.builder()
                .chatRoom(chatRoom)
                .chatReportNickname(nicknamePair.getNickname())
                .chatReportTargetNickname(nicknamePair.getTargetNickname())
                .chatReportContent(reqDto.getChatReportContent())
                .build();

        chatReportRepository.save(chatReport);
    }

    private void checkUserAccessToTicket(String userUUID, ChatRoom chatRoom) {
        if (!chatRoom.getTicket().getOrganizer().getUserUUID().equals(userUUID)
                && !chatRoom.getTicket().getWinner().getUserUUID().equals(userUUID)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
    }

    private NicknamePair assignNicknames(String userUUID, ChatRoom chatRoom) {
        String chatReportNickname = "";
        String chatReportTargetNickname = "";

        User winner = chatRoom.getTicket().getWinner();
        User orgainizer = chatRoom.getTicket().getOrganizer();
        if (winner.getUserUUID().equals(userUUID)) {
            chatReportNickname = winner.getNickname();
            chatReportTargetNickname = orgainizer.getNickname();
        } else if (orgainizer.getUserUUID().equals(userUUID)) {
            chatReportNickname = orgainizer.getNickname();
            chatReportTargetNickname = winner.getNickname();
        }
        return new NicknamePair(chatReportNickname, chatReportTargetNickname);
    }

    private class NicknamePair {
        private String nickname;
        private String targetNickname;

        NicknamePair(String nickname, String targetNickname) {
            this.nickname = nickname;
            this.targetNickname = targetNickname;
        }

        String getNickname() {
            return nickname;
        }

        String getTargetNickname() {
            return targetNickname;
        }
    }
}
