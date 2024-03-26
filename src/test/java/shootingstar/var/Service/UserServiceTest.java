package shootingstar.var.Service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import shootingstar.var.controller.UserController;
import shootingstar.var.dto.req.UserSignupReqDto;
import shootingstar.var.dto.req.WarningListDto;
import shootingstar.var.dto.res.UserReceiveReviewDto;
import shootingstar.var.entity.*;
import shootingstar.var.repository.AuctionRepository;
import shootingstar.var.repository.Review.ReviewRepository;
import shootingstar.var.repository.TicketRepository;
import shootingstar.var.repository.UserRepository;

import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private TicketRepository ticketRepository;

    @Test
    @Transactional
    public void saveReview() throws Exception {
        User user1 = new User(
                "000000001",
                "이재현",
                "dlwogus",
                "+82 10-0000-000",
                "wwwwww@gmail.com",
                "http://k.kakaocdn.net/dn/bWOklw/btsEJdyuAoJ/DgLQ4aHPSPshqJyGPEkzs0/img_640x640.jpg",
                UserType.ROLE_BASIC
        );
        userRepository.save(user1);
        userRepository.flush();

        userService.getProfile("dlwogus");

        System.out.println(user1.getUserUUID());

        Auction auction1 = new Auction(
                user1,
                1000000,
                LocalDateTime.now(),
                "seoul",
                "meetingInfoText",
                "meetingPromiseText",
                "meetingInfoImg",
                "meetingPromiseImg"
        );

        auctionRepository.save(auction1);
        auctionRepository.flush();

        System.out.println(auction1.getUser().getUserUUID());

        Ticket ticket1 = new Ticket(
                auction1,
                user1
        );

        ticketRepository.save(ticket1);
        ticketRepository.flush();

        Review review1 = new Review(
                user1,
                user1,
                "review1 Content",
                0,
                ticket1,
                true
        );

        reviewRepository.save(review1);
        reviewRepository.flush();


        //when
    }

    @Test
    @Transactional
    public void saveUser() throws Exception {
        //given

        //when

        //then


    }
}