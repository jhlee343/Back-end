package shootingstar.var.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shootingstar.var.entity.auction.Auction;
import shootingstar.var.entity.log.DonationLog;
import shootingstar.var.entity.log.PointLog;
import shootingstar.var.entity.Wallet;
import shootingstar.var.entity.chat.ChatRoom;
import shootingstar.var.entity.ticket.TicketMeetingTime;
import shootingstar.var.enums.type.AuctionType;
import shootingstar.var.entity.ScheduledTask;
import shootingstar.var.enums.type.PointOriginType;
import shootingstar.var.enums.type.TaskType;
import shootingstar.var.entity.ticket.Ticket;
import shootingstar.var.entity.User;
import shootingstar.var.exception.CustomException;
import shootingstar.var.exception.ErrorCode;
import shootingstar.var.repository.AuctionRepository;
import shootingstar.var.repository.log.DonationLogRepository;
import shootingstar.var.repository.log.PointLogRepository;
import shootingstar.var.repository.ScheduledTaskRepository;
import shootingstar.var.repository.chat.ChatRoomRepository;
import shootingstar.var.repository.ticket.TicketRepository;
import shootingstar.var.repository.user.UserRepository;
import shootingstar.var.repository.wallet.WalletRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final TicketRepository ticketRepository;
    private final AuctionRepository auctionRepository;
    private final PointLogRepository pointLogRepository;
    private final ScheduledTaskRepository scheduledTaskRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final WalletRepository walletRepository;
    private final DonationLogRepository donationLogRepository;

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
            User user = userRepository.findByUserIdWithPessimisticLock(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            log.info("사용자 증가 전 포인트 : {}", user.getPoint());
            user.increasePoint(BigDecimal.valueOf(auction.getMinBidAmount()));
            log.info("사용자 증가 후 포인트 : {}", user.getPoint());

            PointLog pointLog = PointLog.createPointLogWithDeposit(user, PointOriginType.AUCTION_REGISTRATION_DEPOSIT, BigDecimal.valueOf(auction.getMinBidAmount()));
            pointLogRepository.save(pointLog);

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

        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .ticket(ticket)
                .build();
        chatRoomRepository.save(chatRoom);

        // 경매 타입 낙찰로 변경
        auction.changeAuctionType(AuctionType.SUCCESS);

        // scheduledTask 타입 완료로 변경
        ScheduledTask task = scheduledTaskRepository.findById(scheduledTaskId)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));
        task.changeTaskType(TaskType.COMPLETE);
    }

    @Transactional
    public void completeTicket() {
        log.info("식사권 완료 로직 실행, 현재 시간 : {}", LocalDateTime.now());

        List<Ticket> tickets = ticketRepository.findByTicketIsOpenedTrueAndWinnerIsPushedTrueAndOrganizerIsPushedTrue();
        for (Ticket ticket : tickets) {
            LocalDateTime meetingTime = calculateMeetingTime(ticket);

            // 만남 시작 시간 이후 3일이 지나지 않았을 경우
            if (meetingTime.plusDays(3).isAfter(LocalDateTime.now())) {
                log.info("3일이 지나지 않음");
                continue;
            }

            // 식사권이 신고된 경우
            if (ticket.getReports().size() > 0) {
                log.info("신고당한 식사권");
                continue;
            }

            // 식사권 닫기
            ticket.changeTicketIsOpened(false);

            // 채팅방 닫기
            ticket.getChatRoom().changeChatRoomIsOpened(false);

            // 포인트 정산
            // 경매 주최자에게 70% 지급
            User organizer = userRepository.findByUserUUIDWithPessimisticLock(ticket.getOrganizer().getUserUUID())
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            BigDecimal point = BigDecimal.valueOf(ticket.getAuction().getCurrentHighestBidAmount()).multiply(new BigDecimal(0.7));
            organizer.increasePoint(point);

            PointLog pointLog = PointLog.createPointLogWithDeposit(organizer, PointOriginType.TICKET_COMPLETE, point);
            pointLogRepository.save(pointLog);

            // 기부금액에 5% 추가 => 추후 추가할 예정
            Wallet wallet = walletRepository.findWithPessimisticLock()
                    .orElseThrow(() -> new CustomException(ErrorCode.WALLET_NOT_FOUND));
            BigDecimal donation = BigDecimal.valueOf(ticket.getAuction().getCurrentHighestBidAmount()).multiply(new BigDecimal(0.05));
            wallet.increaseDonation(donation);

            // 기부금액 로그 저장
            DonationLog donationLog = DonationLog.builder()
                    .donorNickname(ticket.getWinner().getNickname())
                    .donationPrice(donation)
                    .totalDonationPrice(wallet.getTotalDonationPrice())
                    .build();
            donationLogRepository.save(donationLog);
        }
    }

    public LocalDateTime calculateMeetingTime(Ticket ticket) {
        LocalDateTime meetingTime;
        List<TicketMeetingTime> ticketMeetingTimes = ticket.getTicketMeetingTimes();
        if (ticketMeetingTimes.get(0).getStartMeetingTime().isAfter(ticketMeetingTimes.get(1).getStartMeetingTime())) {
            meetingTime = ticketMeetingTimes.get(0).getStartMeetingTime();
        } else {
            meetingTime = ticketMeetingTimes.get(1).getStartMeetingTime();
        }
        return meetingTime;
    }
}
