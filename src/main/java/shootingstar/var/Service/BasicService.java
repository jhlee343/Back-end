package shootingstar.var.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import shootingstar.var.dto.req.UserApplyVipDto;
import shootingstar.var.dto.res.TicketListResDto;
import shootingstar.var.dto.res.UserAuctionParticipateList;
import shootingstar.var.dto.res.UserAuctionSuccessList;
import shootingstar.var.enums.type.TicketSortType;
import shootingstar.var.entity.User;
import shootingstar.var.entity.VipApprovalType;
import shootingstar.var.entity.VipInfo;
import shootingstar.var.exception.CustomException;
import shootingstar.var.repository.AuctionRepository;
import shootingstar.var.repository.User.UserRepository;
import shootingstar.var.repository.Vip.VipInfoRepository;
import shootingstar.var.repository.ticket.TicketRepository;

import java.util.Optional;

import static shootingstar.var.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class BasicService {
    private final VipInfoRepository vipInfoRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    public void applyVip(String userUUID , UserApplyVipDto userApplyVipDto){
        User user = findByUserUUID(userUUID);
        VipInfo vipInfo = VipInfo.builder()
                .user(user)
                .vipName(userApplyVipDto.getVipName())
                .vipJob(userApplyVipDto.getVipJob())
                .vipCareer(userApplyVipDto.getVipCareer())
                .vipIntroduce(userApplyVipDto.getVipIntroduce())
                .vipApprovalType(VipApprovalType.STANDBY)
                .vipEvidenceUrl(userApplyVipDto.getVipEvidenceUrl())
                .build();

        vipInfoRepository.save(vipInfo);
    }

    public Page<TicketListResDto> getAllTicketList(String userUUID, TicketSortType ticketSortType, String search, Pageable pageable){
        return ticketRepository.findAllTicketByuserUUID(userUUID, ticketSortType,search,pageable);
    }


    public Page<UserAuctionSuccessList> successBeforeAuctionList(String userUUID, Pageable pageable){
        return auctionRepository.findAllSuccessBeforeByuserUUID(userUUID, pageable);
    //    return null;
    }

    public Page<UserAuctionSuccessList> successAfterAuctionList(String userUUID, Pageable pageable){
        return auctionRepository.findAllSuccessAfterByuserUUID(userUUID, pageable);
        //    return null;
    }
    public Page<UserAuctionParticipateList> participateAuctionList(String userUUID, Pageable pageable){
        return auctionRepository.findAllParticipateByuserUUID(userUUID, pageable);
        //return null;
    }

    public User findByUserUUID(String userUUID) {
        Optional<User> optionalUser = userRepository.findByUserUUID(userUUID);
        if (optionalUser.isEmpty()) {
            throw new CustomException(USER_NOT_FOUND);
        }
        return optionalUser.get();
    }
}
