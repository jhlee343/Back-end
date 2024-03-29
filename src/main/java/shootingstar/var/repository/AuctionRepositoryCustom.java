package shootingstar.var.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shootingstar.var.dto.res.UserAuctionParticipateList;
import shootingstar.var.dto.res.UserAuctionSuccessList;

import java.util.List;

public interface AuctionRepositoryCustom {
    List<UserAuctionSuccessList> findSuccessAfterList(String userUUID, Pageable pageable);
    Page<UserAuctionSuccessList> findAllSuccessAfterByuserUUID(String userUUID, Pageable pageable);

    List<UserAuctionSuccessList> findSuccessBeforeList(String userUUID, Pageable pageable);
    Page<UserAuctionSuccessList> findAllSuccessBeforeByuserUUID(String userUUID, Pageable pageable);

    List<UserAuctionParticipateList> findParticipateList(String userUUID, Pageable pageable);
    Page<UserAuctionParticipateList> findAllParticipateByuserUUID(String userUUID, Pageable pageable);
}
