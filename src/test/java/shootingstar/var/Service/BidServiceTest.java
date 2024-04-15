package shootingstar.var.Service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.Scheduler;
import shootingstar.var.entity.ScheduledTask;
import shootingstar.var.entity.User;
import shootingstar.var.entity.auction.Auction;
import shootingstar.var.exception.CustomException;
import shootingstar.var.repository.AuctionRepository;
import shootingstar.var.repository.BidRepository;
import shootingstar.var.repository.ScheduledTaskRepository;
import shootingstar.var.repository.log.PointLogRepository;
import shootingstar.var.repository.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class BidServiceTest {
    @Mock
    private AuctionRepository auctionRepository;
    @Mock
    private BidRepository bidRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PointLogRepository pointLogRepository;
    @Mock
    private ScheduledTaskRepository scheduledTaskRepository;
    @Mock
    private Scheduler scheduler;

    @InjectMocks
    private BidService bidService;

    @Test
    @DisplayName("응찰하는 사용자가 경매 주최자와 동일한 사용자일 때 에러 발생")
    void validateUserIsOrganizer_ThrowsException() {
        // given
        String organizerUUID = "organizerUUID";
        User organizer = mock(User.class);
        when(organizer.getUserUUID()).thenReturn(organizerUUID);

        Auction auction = mock(Auction.class);
        when(auction.getUser()).thenReturn(organizer);

        // when, then
        Assertions.assertThatThrownBy(() ->
                bidService.validateUserIsOrganizer(organizerUUID, auction))
                .isInstanceOf(CustomException.class)
                .hasMessage("접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("응찰하는 사용자가 경매 주최자와 다른 사용자일 때 성공")
    void validateUserIsOrganizer_DoesNotThrowsException() {
        // given
        String organizerUUID = "organizerUUID";
        User organizer = mock(User.class);
        when(organizer.getUserUUID()).thenReturn(organizerUUID);

        Auction auction = mock(Auction.class);
        when(auction.getUser()).thenReturn(organizer);

        String anotherUUID = "anotherUUID";

        // when, then
        assertThatCode(() ->
                bidService.validateUserIsOrganizer(anotherUUID, auction))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("이전 최고 입찰자가 현재 입찰하는 사용자일 때 에러 발생")
    void validateUserIsCurrentHighestBidder_ThrowsException() {
        // given
        String organizerUUID = "organizerUUID";

        Auction auction = mock(Auction.class);
        when(auction.getCurrentHighestBidderUUID()).thenReturn(organizerUUID);


        // when, then
        Assertions.assertThatThrownBy(() ->
                        bidService.validateUserIsCurrentHighestBidder(organizerUUID, auction))
                .isInstanceOf(CustomException.class)
                .hasMessage("접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("이전 최고 입찰자가 현재 입찰하는 사용자가 아닐 때 성공")
    void validateUserIsCurrentHighestBidder_DoesNotThrowsException() {
        // given
        String organizerUUID = "organizerUUID";

        Auction auction = mock(Auction.class);
        when(auction.getCurrentHighestBidderUUID()).thenReturn(organizerUUID);

        String anotherUUID = "anotherUUID";

        // when, then
        assertThatCode(() ->
                bidService.validateUserIsOrganizer(anotherUUID, auction))
                .doesNotThrowAnyException();
    }

//    @Test
//    @DisplayName("경매가 마감 30분 전이고, 아직 연장되지 않았을 때")
//    void increaseEndTime_DoesNotThrowsException() {
//        // given
//        LocalDateTime closeTime = LocalDateTime.now().plusMinutes(20);
//        Auction auction = mock(Auction.class);
//        when(auction.isExtended()).thenReturn(false);
//        when(auction.getAuctionCloseTime()).thenReturn(closeTime);
//        when(auction.getAuctionId()).thenReturn(1L);
//
//        ScheduledTask task = mock(ScheduledTask.class);
//        when(scheduledTaskRepository.findByAuctionId(1L)).thenReturn(Optional.of(task));
//
//        // when
//        bidService.increaseEndTime(auction);
//
//        // then
//        verify(auction).changeIsExtended(true);
//    }
}