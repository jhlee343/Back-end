package shootingstar.var.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shootingstar.var.entity.Auction;
import shootingstar.var.entity.AuctionType;
import shootingstar.var.entity.User;
import shootingstar.var.exception.CustomException;
import shootingstar.var.exception.ErrorCode;
import shootingstar.var.jwt.JwtTokenProvider;
import shootingstar.var.repository.AuctionRepository;
import shootingstar.var.repository.UserRepository;
import shootingstar.var.dto.req.AuctionCreateReqDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public void create(AuctionCreateReqDto reqDto, HttpServletRequest request) {
        UUID userUUID = jwtTokenProvider.getUserUUIDByRequest(request);

        User findUser = userRepository.findByUserUUID(userUUID)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Auction auction = Auction.builder()
                .user(findUser)
                .minBidAmount(reqDto.getMinBidAmount())
                .meetingDate(LocalDateTime.parse(reqDto.getMeetingDate()))
                .meetingLocation(reqDto.getMeetingLocation())
                .meetingInfoText(reqDto.getMeetingInfoText())
                .meetingPromiseText(reqDto.getMeetingPromiseText())
                .meetingInfoImg(reqDto.getMeetingInfoImg())
                .meetingPromiseImg(reqDto.getMeetingPromiseImg())
                .build();

        auctionRepository.save(auction);
    }

    @Transactional
    public void cancel(UUID auctionUUID, HttpServletRequest request) {
        // uuid에 해당하는 경매가 존재하는지 확인
        Auction findAuction = auctionRepository.findByAuctionUUID(auctionUUID)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        // 찾은 경매가 로그인한 유저가 생성한 게 맞는지 확인
        UUID userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        if (!findAuction.getUser().getUserUUID().equals(userUUID)) {
            throw new CustomException(ErrorCode.AUCTION_ACCESS_DENIED);
        }

        // 경매 타입이 CANCEL인지 확인
        if (findAuction.getAuctionType().equals(AuctionType.CANCEL)) {
            throw new CustomException(ErrorCode.AUCTION_CONFLICT);
        }

        // 경매 타입을 CANCEL로 변경
        findAuction.changeAuctionType(AuctionType.CANCEL);
        log.info("경매가 취소되었습니다. auctionUUID : {}", findAuction.getAuctionUUID());

        // 사용자 포인트에 += 최소입찰금액
        long beforePoint = findAuction.getUser().getPoint();
        log.info("포인트가 추가될 예정입니다. userId : {}, 추가 전 포인트 : {}", findAuction.getUser().getUserId(), beforePoint);
        findAuction.getUser().increasePoint(findAuction.getMinBidAmount());
        long afterPoint = findAuction.getUser().getPoint();
        log.info("포인트가 추가되었습니다. userId : {}, 추가 후 포인트 : {}", findAuction.getUser().getUserId(), afterPoint);
    }
}
