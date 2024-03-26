package shootingstar.var.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shootingstar.var.dto.res.DetailTicketResDto;
import shootingstar.var.entity.Auction;
import shootingstar.var.entity.Ticket;
import shootingstar.var.entity.User;
import shootingstar.var.exception.CustomException;
import shootingstar.var.exception.ErrorCode;
import shootingstar.var.repository.TicketRepository;
import shootingstar.var.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public DetailTicketResDto detailTicket(String ticketUUID, String userUUID) {
        Ticket ticket = ticketRepository.findByTicketUUID(ticketUUID)
                .orElseThrow(() -> new CustomException(ErrorCode.TICKET_NOT_FOUND));

        // 로그인한 사용자가 경매의 낙찰자도 주최자도 아닐 때
        if (!ticket.getWinner().getUserUUID().equals(userUUID) && !ticket.getOrganizer().getUserUUID().equals(userUUID)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        Auction auction = ticket.getAuction();
        User winner = userRepository.findByUserUUID(auction.getCurrentHighestBidderId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return DetailTicketResDto.builder()
                .meetingDate(auction.getMeetingDate())
                .meetingLocation(auction.getMeetingLocation())
                .organizerNickname(auction.getUser().getNickname())
                .winnerNickname(winner.getNickname())
                .winningBid(auction.getCurrentHighestBidAmount())
                .donation(auction.getCurrentHighestBidAmount() * 0.05)
                .meetingInfoText(auction.getMeetingInfoText())
                .meetingPromiseText(auction.getMeetingPromiseText())
                .build();
    }
}
