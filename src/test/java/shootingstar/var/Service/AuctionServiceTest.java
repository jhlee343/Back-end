package shootingstar.var.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import shootingstar.var.dto.req.AuctionCreateReqDto;
import shootingstar.var.entity.auction.Auction;
import shootingstar.var.entity.ScheduledTask;
import shootingstar.var.entity.User;
import shootingstar.var.entity.log.PointLog;
import shootingstar.var.enums.type.AuctionType;
import shootingstar.var.enums.type.PointOriginType;
import shootingstar.var.enums.type.UserType;
import shootingstar.var.exception.CustomException;
import shootingstar.var.repository.AuctionRepository;
import shootingstar.var.repository.ScheduledTaskRepository;
import shootingstar.var.repository.log.PointLogRepository;
import shootingstar.var.repository.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuctionServiceTest {
    @Mock
    private AuctionRepository auctionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PointLogRepository pointLogRepository;
    @Mock
    private ScheduledTaskRepository scheduledTaskRepository;
    @Mock
    private Scheduler scheduler;

    @InjectMocks
    private AuctionService auctionService;

    private User user;
    private AuctionCreateReqDto req = new AuctionCreateReqDto();
    private String userUUID = "userUUID";

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .kakaoId("1234567898")
                .name("홍길동")
                .nickname("hong")
                .phone("01012345678")
                .email("aa@aa.com")
                .profileImgUrl("http://www.naver.com")
                .userType(UserType.ROLE_VIP)
                .build();
        user.increasePoint(BigDecimal.valueOf(200000));

        req.setMinBidAmount(200000);
        req.setMeetingDate("2024-05-11T18:00:00");
        req.setMeetingLocation("대전 대덕구 전민동");
        req.setMeetingInfoText("안녕하세요.");
        req.setMeetingPromiseText("반갑습니다.");
    }

    @Test
    @DisplayName("경매 생성 로직 테스트")
    void createAuction() {
        // given
        when(userRepository.findByUserUUIDWithPessimisticLock(userUUID)).thenReturn(Optional.of(user));
        BigDecimal userPoint = user.getPoint();

        // when
        auctionService.create(req, userUUID);

        // then
        verify(userRepository).findByUserUUIDWithPessimisticLock(userUUID);
        verify(auctionRepository).save(any(Auction.class));
        verify(pointLogRepository).save(any(PointLog.class));
        assertThat(user.getPoint()).isEqualTo(userPoint.subtract(BigDecimal.valueOf(req.getMinBidAmount())));
    }

    @Test
    @DisplayName("경매 생성 후 스케줄링 생성 성공")
    void schedulingCreateTicket_Success() throws SchedulerException {
        // given
        Auction auction = mock(Auction.class);
        when(auction.getAuctionId()).thenReturn(1L);
        when(auction.getAuctionUUID()).thenReturn("auctionUUID");

        User user1 = mock(User.class);
        when(auction.getUser()).thenReturn(user1);
        when(user1.getUserId()).thenReturn(2L);

        // when
        auctionService.schedulingCreateTicket(auction, user1);

        // then
        verify(scheduledTaskRepository).save(any(ScheduledTask.class));
        verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    @DisplayName("경매 생성 후 스케줄링 생성하는 데 에러 발생")
    void schedulingCreateTicket_ThrowsException() throws SchedulerException {
        // given
        Auction auction = mock(Auction.class);
        when(auction.getAuctionId()).thenReturn(1L);
        when(auction.getAuctionUUID()).thenReturn("auctionUUID");

        User user1 = mock(User.class);
        when(auction.getUser()).thenReturn(user1);
        when(user1.getUserId()).thenReturn(2L);

        // when
        doThrow(SchedulerException.class).when(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));

        // then
        assertThatThrownBy(() -> auctionService.schedulingCreateTicket(auction, user))
                .isInstanceOf(CustomException.class)
                .hasMessage("알 수 없는 오류가 발생했습니다.");
    }

    @Test
    @DisplayName("예외 - userUUID에 해당하는 사용자가 없을 때")
    void findNotByUserUUIDWithPessimisticLock() {
        // given

        // when
        when(userRepository.findByUserUUIDWithPessimisticLock(userUUID)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> {
            auctionService.create(req, userUUID);
        }).isInstanceOf(CustomException.class)
                .hasMessage("존재하지 않는 사용자입니다.");
    }

    @Test
    @DisplayName("예외 - 최소 입찰 금액이 만원 단위가 아닐 때")
    void minBidAmountIsNotInTenThousandUnit() {
        // given
        req.setMinBidAmount(223000);

        // when
        when(userRepository.findByUserUUIDWithPessimisticLock(userUUID)).thenReturn(Optional.of(user));

        // then
        assertThatThrownBy(() -> {
            auctionService.create(req, userUUID);
        }).isInstanceOf(CustomException.class)
                .hasMessage("잘못된 형식의 최소 입찰 금액입니다.");
    }

    @Test
    @DisplayName("예외 - 보유 포인트보다 최소 입찰 금액이 더 클 경우")
    void minBidAmountExceedsUserPoint() {
        // given

        // when
        when(userRepository.findByUserUUIDWithPessimisticLock(userUUID)).thenReturn(Optional.of(user));
        user.decreasePoint(user.getPoint());

        // then
        assertThatThrownBy(() -> {
            auctionService.create(req, userUUID);
        }).isInstanceOf(CustomException.class)
                .hasMessage("최소입찰금액은 자신의 보유 포인트보다 적어야 합니다.");
    }

    @Test
    @DisplayName("경매 취소 - 입찰에 참여한 유저가 있을 때, 현재 최고 입찰자에게 현재 최고 입찰 금액을 포인트로 반환")
    void refundHighestBidderOnAuctionCancellation() {
        // given
        User currentHighestBidder = User.builder()
                .kakaoId("1234567898")
                .name("홍길동")
                .nickname("hong")
                .phone("01012345678")
                .email("aa@aa.com")
                .profileImgUrl("http://www.naver.com")
                .userType(UserType.ROLE_BASIC)
                .build();
        String currentHighestBidderUUID = "currentHighestBidderUUID";

        Auction auction = mock(Auction.class);
        when(auction.getCurrentHighestBidderUUID()).thenReturn(currentHighestBidderUUID);

        long highestBidAmount = 200000L;
        when(auction.getCurrentHighestBidAmount()).thenReturn(highestBidAmount);

        when(userRepository.findByUserUUIDWithPessimisticLock(currentHighestBidderUUID)).thenReturn(Optional.of(currentHighestBidder));

        // when
        auctionService.refundHighestBidderOnAuctionCancellation(auction, PointOriginType.VIP_AUCTION_CANCEL);

        // then
        assertThat(currentHighestBidder.getPoint()).isEqualTo(BigDecimal.valueOf(highestBidAmount));
        verify(pointLogRepository).save(any(PointLog.class));
    }

    @Test
    @DisplayName("예외 - auctionUUID에 해당하는 auction을 찾을 수 없을 때")
    void auctionNotExists() {
        // given
        String auctionUUID = "auctionUUID";
        String userType = UserType.ROLE_VIP.toString();

        // when
        when(auctionRepository.findByAuctionUUID(auctionUUID)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> auctionService.cancel(auctionUUID, userUUID, userType))
                .isInstanceOf(CustomException.class)
                .hasMessage("존재하지 않는 경매입니다.");
    }

    @Test
    @DisplayName("예외 - 경매 주최자의 userUUID가 아니면서 권한이 ADMIN도 아닐 때")
    void notOrganizer() {
        // given
        String auctionUUID = "auctionUUID";

        User organizer = mock(User.class);

        Auction auction = Auction.builder()
                .user(organizer)
                .minBidAmount(200000)
                .meetingDate(LocalDateTime.now().plusDays(30))
                .meetingLocation("대전시 대덕구 전민동")
                .meetingInfoText("안녕하세요.")
                .meetingPromiseText("반갑습니다.")
                .build();

        when(auctionRepository.findByAuctionUUID(auctionUUID)).thenReturn(Optional.of(auction));

        // when
        when(organizer.getUserUUID()).thenReturn("user1");

        // then
        assertThatThrownBy(() -> auctionService.cancel(auctionUUID, userUUID, ""))
                .isInstanceOf(CustomException.class)
                .hasMessage("접근 권한이 없습니다.");

        assertThatThrownBy(() -> auctionService.cancel(auctionUUID, userUUID, UserType.ROLE_VIP.toString()))
                .isInstanceOf(CustomException.class)
                .hasMessage("접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("예외 - 경매 타입이 진행중이 아닐 때")
    void auctionTypeNotProgress() {
        // given
        String auctionUUID = "auctionUUID";

        User organizer = mock(User.class);

        Auction auction = Auction.builder()
                .user(organizer)
                .minBidAmount(200000)
                .meetingDate(LocalDateTime.now().plusDays(30))
                .meetingLocation("대전시 대덕구 전민동")
                .meetingInfoText("안녕하세요.")
                .meetingPromiseText("반갑습니다.")
                .build();

        when(auctionRepository.findByAuctionUUID(auctionUUID)).thenReturn(Optional.of(auction));
        when(organizer.getUserUUID()).thenReturn(userUUID);

        // when, then
        auction.changeAuctionType(AuctionType.SUCCESS);
        assertThatThrownBy(() -> auctionService.cancel(auctionUUID, userUUID, UserType.ROLE_VIP.toString()))
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 처리된 경매입니다.");

        auction.changeAuctionType(AuctionType.CANCEL);
        assertThatThrownBy(() -> auctionService.cancel(auctionUUID, userUUID, UserType.ROLE_VIP.toString()))
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 처리된 경매입니다.");

        auction.changeAuctionType(AuctionType.INVALIDITY);
        assertThatThrownBy(() -> auctionService.cancel(auctionUUID, userUUID, UserType.ROLE_VIP.toString()))
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 처리된 경매입니다.");
    }
}