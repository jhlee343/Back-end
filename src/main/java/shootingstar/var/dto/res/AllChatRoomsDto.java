package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AllChatRoomsDto {
    private String chatRoomUUID;
    private String organizerName;
    private String organizerNickname;
    private String winnerName;
    private String winnerNickname;
    private LocalDateTime meetingDate;
    private boolean chatRoomIsOpened;

    @QueryProjection
    public AllChatRoomsDto(String chatRoomUUID, String organizerName, String organizerNickname, String winnerName, String winnerNickname, LocalDateTime meetingDate, boolean chatRoomIsOpened) {
        this.chatRoomUUID = chatRoomUUID;
        this.organizerName = organizerName;
        this.organizerNickname = organizerNickname;
        this.winnerName = winnerName;
        this.winnerNickname = winnerNickname;
        this.meetingDate = meetingDate;
        this.chatRoomIsOpened = chatRoomIsOpened;
    }
}
