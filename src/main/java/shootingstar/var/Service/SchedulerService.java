package shootingstar.var.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shootingstar.var.entity.Auction;
import shootingstar.var.entity.AuctionType;
import shootingstar.var.entity.Ticket;
import shootingstar.var.entity.User;
import shootingstar.var.exception.CustomException;
import shootingstar.var.exception.ErrorCode;
import shootingstar.var.repository.AuctionRepository;
import shootingstar.var.repository.TicketRepository;
import shootingstar.var.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final TicketRepository ticketRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createTicketAndAuctionTypeSuccess(Long auctionId, Long userId) {
        log.info("스케쥴링 시작");
        log.info("스레드 이름 : {}", Thread.currentThread().getName());
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        // 아무도 경매에 참여하지 않았을 경우
        if (auction.getCurrentHighestBidderId() == null) {
            log.info("아무도 경매에 참여하지 않음");
            // 경매 타입 유찰로 변경
            log.info("변경 전 경매 타입 : {}", auction.getAuctionType());
            auction.changeAuctionType(AuctionType.INVALIDITY);
            log.info("변경 후 경매 타입 : {}", auction.getAuctionType());

            // 경매 생성자에게 최소 입찰 금액만큼의 포인트 돌려주기
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            log.info("사용자 증가 전 포인트 : {}", user.getPoint());
            user.increasePoint(auction.getMinBidAmount());
            log.info("사용자 증가 후 포인트 : {}", user.getPoint());
            return;
        }

        log.info("식사권 생성 & 경매 타입 변경");
        // 한 명 이라도 경매에 참여했을 경우
        User user = userRepository.findByUserUUID(auction.getCurrentHighestBidderId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 식사권 생성
        Ticket ticket = new Ticket(auction, user);
        ticketRepository.save(ticket);

        // 경매 타입 낙찰로 변경
        Auction findAuction = auctionRepository.findById(auction.getAuctionId())
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));
        findAuction.changeAuctionType(AuctionType.SUCCESS);
    }
}
