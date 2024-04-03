package shootingstar.var.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import shootingstar.var.dto.req.UserApplyVipDto;
import shootingstar.var.dto.res.TicketListResDto;
import shootingstar.var.dto.res.UserAuctionParticipateResDto;
import shootingstar.var.dto.res.UserAuctionSuccessResDto;
import shootingstar.var.enums.type.TicketSortType;
import shootingstar.var.entity.User;
import shootingstar.var.entity.VipApprovalType;
import shootingstar.var.entity.VipInfo;
import shootingstar.var.exception.CustomException;
import shootingstar.var.repository.AuctionRepository;
import shootingstar.var.repository.user.UserRepository;
import shootingstar.var.repository.vip.VipInfoRepository;
import shootingstar.var.repository.ticket.TicketRepository;

import java.util.Optional;

import static shootingstar.var.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class BasicUserService {
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
        return ticketRepository.findAllTicketByuserUUID(userUUID, ticketSortType, search, pageable);
    }


    public Page<UserAuctionSuccessResDto> successBeforeAuctionList(String userUUID, Pageable pageable){
        return auctionRepository.findAllSuccessBeforeByUserUUID(userUUID, pageable);
    }

    public Page<UserAuctionSuccessResDto> successAfterAuctionList(String userUUID, Pageable pageable){
        return auctionRepository.findAllSuccessAfterByUserUUID(userUUID, pageable);
    }
    public Page<UserAuctionParticipateResDto> participateAuctionList(String userUUID, Pageable pageable){
        return auctionRepository.findAllParticipateByUserUUID(userUUID, pageable);
    }

    public User findByUserUUID(String userUUID) {
        Optional<User> optionalUser = userRepository.findByUserUUID(userUUID);
        if (optionalUser.isEmpty()) {
            throw new CustomException(USER_NOT_FOUND);
        }
        return optionalUser.get();
    }
}
