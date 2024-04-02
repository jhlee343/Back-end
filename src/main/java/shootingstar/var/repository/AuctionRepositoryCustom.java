package shootingstar.var.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shootingstar.var.dto.res.UserAuctionInvalidityResDto;
import shootingstar.var.dto.res.UserAuctionParticipateList;
import shootingstar.var.dto.res.UserAuctionSuccessList;

import java.util.List;

public interface AuctionRepositoryCustom {
    Page<UserAuctionSuccessList> findAllSuccessAfterByUserUUID(String userUUID, Pageable pageable);
    Page<UserAuctionSuccessList> findAllSuccessBeforeByUserUUID(String userUUID, Pageable pageable);

    List<UserAuctionParticipateList> findParticipateList(String userUUID, Pageable pageable);
    Page<UserAuctionParticipateList> findAllParticipateByUserUUID(String userUUID, Pageable pageable);

    Page<UserAuctionSuccessList> findAllVipSuccessByUserUUID(String userUUID, Pageable pageable);
    Page<UserAuctionParticipateList> findAllVipProgressByUserUUID(String userUUID, Pageable pageable);
    Page<UserAuctionInvalidityResDto> findAllVipInvalidityByUserUUID(String userUUID,Pageable pageable);
}
