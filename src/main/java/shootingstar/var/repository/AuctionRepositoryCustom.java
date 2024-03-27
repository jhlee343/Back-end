package shootingstar.var.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shootingstar.var.dto.res.UserAuctionParticipateList;
import shootingstar.var.dto.res.UserAuctionSuccessList;

import java.util.List;

public interface AuctionRepositoryCustom {
    List<UserAuctionSuccessList> findSuccessList(String userUUID, Pageable pageable);
    Page<UserAuctionSuccessList> findAllSuccessByuserUUID(String userUUID, Pageable pageable);

    List<UserAuctionParticipateList> findParticipateList(String userUUID, Pageable pageable);
    Page<UserAuctionParticipateList> findAllParticipateByuserUUID(String userUUID, Pageable pageable);
}
