package shootingstar.var.Service;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shootingstar.var.entity.Auction;
import shootingstar.var.enums.type.AuctionType;
import shootingstar.var.entity.ScheduledTask;
import shootingstar.var.enums.type.TaskType;
import shootingstar.var.entity.ticket.Ticket;
import shootingstar.var.entity.User;
import shootingstar.var.exception.CustomException;
import shootingstar.var.exception.ErrorCode;
import shootingstar.var.repository.AuctionRepository;
import shootingstar.var.repository.ScheduledTaskRepository;
import shootingstar.var.repository.ticket.TicketRepository;
import shootingstar.var.repository.User.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final TicketRepository ticketRepository;
    private final AuctionRepository auctionRepository;
    private final ScheduledTaskRepository scheduledTaskRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createTicketAndAuctionTypeSuccess(Long auctionId, Long userId, Long scheduledTaskId) {
        log.info("스케쥴링 시작");
        log.info("스레드 이름 : {}", Thread.currentThread().getName());
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        // 아무도 경매에 참여하지 않았을 경우
        if (auction.getCurrentHighestBidderUUID() == null) {
            log.info("아무도 경매에 참여하지 않음");
            // 경매 타입 유찰로 변경
            log.info("변경 전 경매 타입 : {}", auction.getAuctionType());
            auction.changeAuctionType(AuctionType.INVALIDITY);
            log.info("변경 후 경매 타입 : {}", auction.getAuctionType());

            // 경매 생성자에게 최소 입찰 금액만큼의 포인트 돌려주기
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            log.info("사용자 증가 전 포인트 : {}", user.getPoint());
            user.increasePoint(BigDecimal.valueOf(auction.getMinBidAmount()));
            log.info("사용자 증가 후 포인트 : {}", user.getPoint());

            ScheduledTask task = scheduledTaskRepository.findById(scheduledTaskId)
                    .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));
            task.changeTaskType(TaskType.COMPLETE);
            return;
        }

        log.info("식사권 생성 & 경매 타입 변경");
        // 한 명 이라도 경매에 참여했을 경우
        User winner = userRepository.findByUserUUID(auction.getCurrentHighestBidderUUID())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 식사권 생성
        Ticket ticket = new Ticket(auction, winner, auction.getUser());
        ticketRepository.save(ticket);

        // 경매 타입 낙찰로 변경
        Auction findAuction = auctionRepository.findById(auction.getAuctionId())
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));
        findAuction.changeAuctionType(AuctionType.SUCCESS);

        // scheduledTask 타입 완료로 변경
        ScheduledTask task = scheduledTaskRepository.findById(scheduledTaskId)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));
        task.changeTaskType(TaskType.COMPLETE);
    }
}
