package shootingstar.var.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shootingstar.var.dto.res.ProgressAuctionResDto;
import shootingstar.var.dto.res.UserAuctionParticipateList;
import shootingstar.var.dto.res.UserAuctionSuccessList;
import shootingstar.var.enums.type.AuctionSortType;

import java.util.List;

public interface AuctionRepositoryCustom {
    List<UserAuctionSuccessList> findSuccessAfterList(String userUUID, Pageable pageable);
    Page<UserAuctionSuccessList> findAllSuccessAfterByUserUUID(String userUUID, Pageable pageable);

    List<UserAuctionSuccessList> findSuccessBeforeList(String userUUID, Pageable pageable);
    Page<UserAuctionSuccessList> findAllSuccessBeforeByUserUUID(String userUUID, Pageable pageable);

    List<UserAuctionParticipateList> findParticipateList(String userUUID, Pageable pageable);
    Page<UserAuctionParticipateList> findAllParticipateByUserUUID(String userUUID, Pageable pageable);

    Page<ProgressAuctionResDto> findProgressGeneralAuction(Pageable pageable, AuctionSortType sortType, String search);
}
