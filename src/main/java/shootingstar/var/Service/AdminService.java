package shootingstar.var.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import shootingstar.var.dto.res.*;
import shootingstar.var.entity.*;
import shootingstar.var.entity.chat.ChatRoom;
import shootingstar.var.entity.ticket.Ticket;
import shootingstar.var.enums.status.ExchangeStatus;
import shootingstar.var.enums.type.UserType;
import shootingstar.var.exception.CustomException;
import shootingstar.var.exception.ErrorCode;
import shootingstar.var.jwt.JwtTokenProvider;
import shootingstar.var.jwt.TokenInfo;
import shootingstar.var.repository.BanRepository;
import shootingstar.var.repository.admin.AdminRepository;
import shootingstar.var.repository.banner.BannerRepository;
import shootingstar.var.repository.chat.ChatRoomRepository;
import shootingstar.var.repository.exchange.ExchangeRepository;
import shootingstar.var.repository.review.ReviewRepository;
import shootingstar.var.repository.ticket.TicketRepository;
import shootingstar.var.repository.user.UserRepository;
import shootingstar.var.repository.vip.VipInfoRepository;
import shootingstar.var.repository.wallet.WalletRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final BanRepository banRepository;
    private final VipInfoRepository vipInfoRepository;
    private final TicketRepository ticketRepository;
    private final ReviewRepository reviewRepository;
    private final ExchangeRepository exchangeRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final BannerRepository bannerRepository;
    private final WalletRepository walletRepository;

    @Value("${admin-secret-signup-key}")
    private String adminSignupSecretKey;

    public void signup(String id, String password, String nickname, String secretKey) {
        if (!secretKey.equals(adminSignupSecretKey)) {
            throw new CustomException(ErrorCode.INCORRECT_VALUE_ADMIN_SECRET_KEY);
        }
        if (adminRepository.existsByAdminLoginId(id)) {
            throw new CustomException(ErrorCode.DUPLICATE_ADMIN_ID);
        }
        if (adminRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorCode.DUPLICATE_ADMIN_NICKNAME);
        }
        String encodePassword = passwordEncoder.encode(password); // 패스워드 암호화
        Admin admin = new Admin(id, encodePassword, nickname);
        adminRepository.save(admin);
    }

    public TokenInfo login(String id, String password) {
        Authentication authentication;
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(id, password);
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.ADMIN_LOGIN_FAILED);
        }
        return tokenProvider.generateToken(authentication);
    }

    public Page<AllUsersDto> getAllUsers(String search, Pageable pageable) {
        return userRepository.findAllUsers(search, pageable);
    }

    @Transactional
    public void warning(String userUUID) {
        User user = userRepository.findByUserUUID(userUUID)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getWarningCount() >= 3) {
            // 경고 횟수가 이미 3인 유저
            throw new CustomException(ErrorCode.ALREADY_BANNED_USER);
        }

        // 경고 횟수 + 1
        user.setWarningCount(user.getWarningCount() + 1);
        userRepository.save(user);

        // 경고 3회 시 유저 밴
        if (user.getWarningCount() >= 3) {
            Ban ban = new Ban(
                    user,
                    user.getKakaoId()
            );
            banRepository.save(ban);
        }
    }

    public Page<AllVipInfosDto> getAllVipInfos(String search, Pageable pageable) {
        return vipInfoRepository.findAllVipInfos(search, pageable);
    }

    public AllVipInfosDto getVipInfoDetail(String vipInfoUUID) {
        VipInfo vipInfo = vipInfoRepository.findByVipInfoUUID(vipInfoUUID)
                .orElseThrow(() -> new CustomException(ErrorCode.VIP_INFO_NOT_FOUND));

        return new AllVipInfosDto(
                vipInfo.getVipInfoUUID(),
                vipInfo.getVipName(),
                vipInfo.getVipJob(),
                vipInfo.getVipCareer(),
                vipInfo.getVipIntroduce(),
                vipInfo.getVipEvidenceUrl()
        );
    }

    @Transactional
    public void approveVipInfo(String vipInfoUUID) {
        VipInfo vipInfo = vipInfoRepository.findByVipInfoUUID(vipInfoUUID)
                .orElseThrow(() -> new CustomException(ErrorCode.VIP_INFO_NOT_FOUND));
        User user = vipInfo.getUser();

        if (vipInfo.getVipApproval() != VipApprovalType.STANDBY) {
            throw new CustomException(ErrorCode.VIP_INFO_ALREADY_HANDLED);
        }

        vipInfo.changeVipApproval(VipApprovalType.APPROVE);
        vipInfoRepository.save(vipInfo);

        // 유저 등급 VIP로 변환
        user.changeUserType(UserType.ROLE_VIP);
        userRepository.save(user);
    }

    @Transactional
    public void refusalVipInfo(String vipInfoUUID) {
        VipInfo vipInfo = vipInfoRepository.findByVipInfoUUID(vipInfoUUID)
                .orElseThrow(() -> new CustomException(ErrorCode.VIP_INFO_NOT_FOUND));

        if (vipInfo.getVipApproval() != VipApprovalType.STANDBY) {
            throw new CustomException(ErrorCode.VIP_INFO_ALREADY_HANDLED);
        }

        vipInfo.changeVipApproval(VipApprovalType.REFUSAL);
        vipInfoRepository.save(vipInfo);
    }

    public Page<AllTicketsDto> getAllTickets(String search, Pageable pageable) {
        return ticketRepository.findAllTickets(search, pageable);
    }

    @Transactional
    public void changeTicket(String ticketUUID) {
        Ticket ticket = ticketRepository.findByTicketUUID(ticketUUID)
                .orElseThrow(() -> new CustomException(ErrorCode.TICKET_NOT_FOUND));

        if (!ticket.isTicketIsOpened()) {
            throw new CustomException(ErrorCode.ALREADY_TICKET_CANCEL_CONFLICT);
        }

        ticket.getChatRoom().changeChatRoomIsOpened(false);
        ticket.changeTicketIsOpened(false);
    }

    public Page<AllChatRoomsDto> getAllChatRooms(String search, Pageable pageable) {
        return chatRoomRepository.findAllChatRooms(search, pageable);
    }

    @Transactional
    public void changeChat(String chatRoomUUID) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomUUID(chatRoomUUID)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        if (!chatRoom.isChatRoomIsOpened()) {
            throw new CustomException(ErrorCode.CHAT_ROOM_ACCESS_DENIED);
        }

        chatRoom.changeChatRoomIsOpened(false);
    }

    public Page<AllReviewsDto> getAllReviews(String search, Pageable pageable) {
        return reviewRepository.findAllReviews(search, pageable);
    }

    @Transactional
    public void changeReview(String reviewUUID) {
        Review review = reviewRepository.findByReviewUUID(reviewUUID)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.isShowed()) {
            throw new CustomException(ErrorCode.REVIEW_ALREADY_HIDDEN);
        }

        review.changeIsShowed(false);
    }

    public Page<AllExchangesDto> getAllExchanges(String search, Pageable pageable) {
        return exchangeRepository.findAllExchanges(search, pageable);
    }

    @Transactional
    public void approveExchange(String exchangeUUID) {
        Exchange exchange = exchangeRepository.findByExchangeUUID(exchangeUUID)
                .orElseThrow(RuntimeException::new); // EXCHANGE_NOT_FOUND

        Wallet wallet = walletRepository.findWithPessimisticLock()
                .orElseThrow(() -> new CustomException(ErrorCode.WALLET_NOT_FOUND));

        User user = exchange.getUser();
        BigDecimal exchangePoint = BigDecimal.valueOf(exchange.getExchangePoint());
        BigDecimal userPoint = user.getPoint();

        if (userPoint.compareTo(exchangePoint) < 0) {
            throw new RuntimeException(); // 유저의 보유 포인트가 환전 신청할 포인트보다 작을 때
        }

        if (exchange.getExchangeStatus() != ExchangeStatus.STANDBY) {
            throw new RuntimeException(); // 이미 승인 또는 반려된 환전 신청
        }

        exchange.changeExchangeStatus(ExchangeStatus.APPROVE);
        exchangeRepository.save(exchange);

        // 유저 포인트 차감
        user.decreasePoint(exchangePoint);
        userRepository.save(user);

        // 관리자 포인트 차감
        wallet.decreaseCash(exchangePoint);
        walletRepository.save(wallet);
    }

    @Transactional
    public void refusalExchange(String exchangeUUID) {
        Exchange exchange = exchangeRepository.findByExchangeUUID(exchangeUUID)
                .orElseThrow(RuntimeException::new); // EXCHANGE_NOT_FOUND

        if (exchange.getExchangeStatus() != ExchangeStatus.STANDBY) {
            throw new RuntimeException(); // 이미 승인 또는 반려된 환전 신청
        }

        exchange.changeExchangeStatus(ExchangeStatus.REFUSAL);
    }

    public void addBanner(String bannerImgUrl, String targetUrl) {
        Banner banner = new Banner(bannerImgUrl, targetUrl);
        bannerRepository.save(banner);
    }

    public void editBanner(String bannerUUID, String targetUrl) {
        Banner banner = bannerRepository.findByBannerUUID(bannerUUID)
                .orElseThrow(RuntimeException::new);
        banner.changeTargetUrl(targetUrl);
        bannerRepository.save(banner);
    }

    public void deleteBanner(String bannerUUID) {
        Banner banner = bannerRepository.findByBannerUUID(bannerUUID)
                .orElseThrow(RuntimeException::new);
        bannerRepository.delete(banner);
    }

}
