package shootingstar.var.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import shootingstar.var.entity.auction.Auction;
import shootingstar.var.entity.User;
import shootingstar.var.enums.type.UserType;
import shootingstar.var.repository.AuctionRepository;
import shootingstar.var.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

@SpringBootTest
class ParticipatingAuctionRedisUtilTest {

    @Autowired
    private ParticipatingAuctionRedisUtil participatingAuctionRedisUtil;

    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    public void redisTest() throws Exception {
        //given
        User vip = new User("22", "실명", "유명인", "000-0000-0000", "test@ttt.com", "helloUrl", UserType.ROLE_VIP);
        User basic = new User("33", "실명", "일반인", "000-0000-0000", "test@ttt.com", "helloUrl", UserType.ROLE_BASIC);

        userRepository.save(vip);
        userRepository.save(basic);

        userRepository.flush();

        Auction progressAuction = new Auction(vip, 100000L, LocalDateTime.now(), "위치", "정보", "약속", "img", "img");
        Auction progressAuction1 = new Auction(vip, 100000L, LocalDateTime.now(), "위치", "정보", "약속", "img", "img");
        Auction progressAuction2 = new Auction(vip, 100000L, LocalDateTime.now(), "위치", "정보", "약속", "img", "img");
        Auction progressAuction3 = new Auction(vip, 100000L, LocalDateTime.now(), "위치", "정보", "약속", "img", "img");
        Auction progressAuction4 = new Auction(vip, 100000L, LocalDateTime.now(), "위치", "정보", "약속", "img", "img");

        auctionRepository.save(progressAuction);
        auctionRepository.save(progressAuction1);
        auctionRepository.save(progressAuction2);
        auctionRepository.save(progressAuction3);
        auctionRepository.save(progressAuction4);

        auctionRepository.flush();

        //when
        LocalDateTime createdTime = progressAuction.getCreatedTime();
        long epochMilliSeconds = createdTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long expiredMilliSeconds = epochMilliSeconds + (3 * 24 * 60 * 60 * 1000);

        participatingAuctionRedisUtil.addParticipation(basic.getUserId().toString(), progressAuction.getAuctionId().toString(), expiredMilliSeconds);

        LocalDateTime createdTime1 = progressAuction1.getCreatedTime();
        long epochMilli = createdTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long expiredMilliSeconds1 = epochMilli + (3 * 24 * 60 * 60 * 1000);

        participatingAuctionRedisUtil.addParticipation(basic.getUserId().toString(), progressAuction1.getAuctionId().toString(), expiredMilliSeconds1);

        //then
        Set<String> participationList = participatingAuctionRedisUtil.getParticipationList(basic.getUserId().toString());
        System.out.println(participationList);
        Assertions.assertThat(participationList).size().isEqualTo(2);

        participatingAuctionRedisUtil.deleteAll();
    }

}