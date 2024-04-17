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
import shootingstar.var.entity.chat.ChatReport;
import shootingstar.var.entity.chat.ChatRoom;
import shootingstar.var.entity.log.PointLog;
import shootingstar.var.entity.ticket.Ticket;
import shootingstar.var.entity.ticket.TicketReport;
import shootingstar.var.enums.status.ChatReportStatus;
import shootingstar.var.enums.status.ExchangeStatus;
import shootingstar.var.enums.status.TicketReportStatus;
import shootingstar.var.enums.type.PointOriginType;
import shootingstar.var.enums.type.UserType;
import shootingstar.var.exception.CustomException;
import shootingstar.var.exception.ErrorCode;
import shootingstar.var.jwt.JwtTokenProvider;
import shootingstar.var.jwt.TokenInfo;
import shootingstar.var.repository.BanRepository;
import shootingstar.var.repository.admin.AdminRepository;
import shootingstar.var.repository.banner.BannerRepository;
import shootingstar.var.repository.chat.ChatReportRepository;
import shootingstar.var.repository.chat.ChatRoomRepository;
import shootingstar.var.repository.exchange.ExchangeRepository;
import shootingstar.var.repository.log.PointLogRepository;
import shootingstar.var.repository.review.ReviewRepository;
import shootingstar.var.repository.reviewReport.ReviewReportRepository;
import shootingstar.var.repository.ticket.TicketReportRepository;
import shootingstar.var.repository.ticket.TicketRepository;
import shootingstar.var.repository.user.UserRepository;
import shootingstar.var.repository.vip.VipInfoRepository;
import shootingstar.var.repository.wallet.WalletRepository;
import shootingstar.var.repository.warning.WarningRepository;

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
    private final PointLogRepository pointLogRepository;
    private final ReviewReportRepository reviewReportRepository;
    private final ChatReportRepository chatReportRepository;
    private final TicketReportRepository ticketReportRepository;
    private final WarningRepository warningRepository;

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
                .orElseThrow(() -> new CustomException(ErrorCode.EXCHANGE_NOT_FOUND));

        Wallet wallet = walletRepository.findWithPessimisticLock()
                .orElseThrow(() -> new CustomException(ErrorCode.WALLET_NOT_FOUND));

        BigDecimal exchangePoint = BigDecimal.valueOf(exchange.getExchangePoint());

        if (exchange.getExchangeStatus() != ExchangeStatus.STANDBY) {
            throw new CustomException(ErrorCode.EXCHANGE_ALREADY_HANDLED);
        }

        exchange.changeExchangeStatus(ExchangeStatus.APPROVE);
        exchangeRepository.save(exchange);

        // 관리자 지갑 포인트 차감
        wallet.decreaseCash(exchangePoint);
        walletRepository.save(wallet);
    }

    @Transactional
    public void refusalExchange(String exchangeUUID) {
        Exchange exchange = exchangeRepository.findByExchangeUUID(exchangeUUID)
                .orElseThrow(() -> new CustomException(ErrorCode.EXCHANGE_NOT_FOUND));

        User user = exchange.getUser();
        BigDecimal exchangePoint = BigDecimal.valueOf(exchange.getExchangePoint());

        if (exchange.getExchangeStatus() != ExchangeStatus.STANDBY) {
            throw new CustomException(ErrorCode.EXCHANGE_ALREADY_HANDLED);
        }

        exchange.changeExchangeStatus(ExchangeStatus.REFUSAL);
        exchangeRepository.save(exchange);

        // 환전 신청 반려 시 유저한테 포인트 돌려주기
        user.increasePoint(exchangePoint);
        userRepository.save(user);

        // 포인트 로그
        PointLog pointLog = PointLog.createPointLogWithDeposit(user, PointOriginType.EXCHANGE, exchangePoint);
        pointLogRepository.save(pointLog);
    }

    public void addBanner(String bannerImgUrl, String targetUrl) {
        Banner banner = new Banner(bannerImgUrl, targetUrl);
        bannerRepository.save(banner);
    }

    public void editBanner(String bannerUUID, String targetUrl) {
        Banner banner = bannerRepository.findByBannerUUID(bannerUUID)
                .orElseThrow(() -> new CustomException(ErrorCode.BANNER_NOT_FOUND));
        banner.changeTargetUrl(targetUrl);
        bannerRepository.save(banner);
    }

    public void deleteBanner(String bannerUUID) {
        Banner banner = bannerRepository.findByBannerUUID(bannerUUID)
                .orElseThrow(() -> new CustomException(ErrorCode.BANNER_NOT_FOUND));
        bannerRepository.delete(banner);
    }

    public Page<AllReviewReportsDto> getAllReviewReports(String search, Pageable pageable) {
        return reviewReportRepository.findAllReviewReports(search, pageable);
    }

    @Transactional
    public void approveReviewReport(String reviewReportUUID) {
        ReviewReport reviewReport = reviewReportRepository.findByReviewReportUUID(reviewReportUUID)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_REPORT_NOT_FOUND));

        Review review = reviewReport.getReview();
        User user = review.getWriter();

        if (reviewReport.getReviewReportStatus() != ReviewReportStatus.STANDBY) {
            throw new CustomException(ErrorCode.REVIEW_REPORT_ALREADY_HANDLED);
        }

        reviewReport.changeReviewReportStatus(ReviewReportStatus.APPROVE);
        reviewReportRepository.save(reviewReport);

        // 신고 승인 시 리뷰 숨김처리
        review.changeIsShowed(false);
        reviewRepository.save(review);

        // 유저의 경고 내역에 추가
        Warning warning = Warning.builder()
                .user(user)
                .warningContent(reviewReport.getReviewReportContent())
                .build();

        warningRepository.save(warning);

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

    @Transactional
    public void refusalReviewReport(String reviewReportUUID) {
        ReviewReport reviewReport = reviewReportRepository.findByReviewReportUUID(reviewReportUUID)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_REPORT_NOT_FOUND));

        if (reviewReport.getReviewReportStatus() != ReviewReportStatus.STANDBY) {
            throw new CustomException(ErrorCode.REVIEW_REPORT_ALREADY_HANDLED);
        }

        reviewReport.changeReviewReportStatus(ReviewReportStatus.REFUSAL);
        reviewReportRepository.save(reviewReport);
    }

    public Page<AllChatReportsDto> getAllChatReports(String search, Pageable pageable) {
        return chatReportRepository.findAllChatReports(search, pageable);
    }

    @Transactional
    public void approveChatReport(String chatReportUUID) {
        ChatReport chatReport = chatReportRepository.findByChatReportUUID(chatReportUUID)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_REPORT_NOT_FOUND));

        ChatRoom chatRoom = chatReport.getChatRoom();
        User winner = chatRoom.getTicket().getWinner();
        User organizer = chatRoom.getTicket().getOrganizer();
        // 신고자가 낙찰자면 주최자 신고, 신고자가 주최자면 낙찰자 신고
        User user = (chatReport.getChatReportNickname().equals(winner.getNickname())) ?
                organizer : winner;

        if (chatReport.getChatReportStatus() != ChatReportStatus.STANDBY) {
            throw new CustomException(ErrorCode.CHAT_REPORT_ALREADY_HANDLED);
        }

        chatReport.changeChatReportStatus(ChatReportStatus.APPROVE);
        chatReportRepository.save(chatReport);

        // 신고 승인 시 채팅방 닫음
        chatRoom.changeChatRoomIsOpened(false);
        chatRoomRepository.save(chatRoom);

        // 유저의 경고 내역에 추가
        Warning warning = Warning.builder()
                .user(user)
                .warningContent(chatReport.getChatReportContent())
                .build();

        warningRepository.save(warning);

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

    @Transactional
    public void refusalChatReport(String chatReportUUID) {
        ChatReport chatReport = chatReportRepository.findByChatReportUUID(chatReportUUID)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_REPORT_NOT_FOUND));

        if (chatReport.getChatReportStatus() != ChatReportStatus.STANDBY) {
            throw new CustomException(ErrorCode.CHAT_REPORT_ALREADY_HANDLED);
        }

        chatReport.changeChatReportStatus(ChatReportStatus.REFUSAL);
        chatReportRepository.save(chatReport);
    }

    public Page<AllTicketReportsDto> getAllTicketReports(String search, Pageable pageable) {
        return ticketReportRepository.findAllTicketReports(search, pageable);
    }

    @Transactional
    public void approveTicketReport(String ticketReportUUID) {
        TicketReport ticketReport = ticketReportRepository.findByTicketReportUUID(ticketReportUUID)
                .orElseThrow(() -> new CustomException(ErrorCode.TICKET_REPORT_NOT_FOUND));

        Ticket ticket = ticketReport.getTicket();
        User winner = ticket.getWinner();
        User organizer = ticket.getOrganizer();

        if (ticketReport.getTicketReportStatus() != TicketReportStatus.STANDBY) {
            throw new CustomException(ErrorCode.TICKET_REPORT_ALREADY_HANDLED);
        }

        ticketReport.changeTicketReportStatus(TicketReportStatus.APPROVE);
        ticketReportRepository.save(ticketReport);

        // 신고 승인 시 식사권 닫음
        ticket.changeTicketIsOpened(false);
        ticketRepository.save(ticket);

        // 신고자가 낙찰자면 낙찰자에게 낙찰금 100% 전달
        if (ticketReport.getTicketReportNickname().equals(winner.getNickname())) {
            BigDecimal point = BigDecimal.valueOf(ticket.getAuction().getCurrentHighestBidAmount());
            winner.increasePoint(point);
            PointLog pointLog = PointLog.createPointLogWithDeposit(winner, PointOriginType.WINNER_TICKET_REPORT, point);
            pointLogRepository.save(pointLog);
        }
        // 신고자가 주최자면 주최자에게 낙찰금 70% 전달
        else if (ticketReport.getTicketReportNickname().equals(organizer.getNickname())) {
            BigDecimal point = BigDecimal.valueOf(ticket.getAuction().getCurrentHighestBidAmount()).multiply(BigDecimal.valueOf(0.7));
            organizer.increasePoint(point);
            PointLog pointLog = PointLog.createPointLogWithDeposit(organizer, PointOriginType.ORGANIZER_TICKET_REPORT, point);
            pointLogRepository.save(pointLog);
        }

        // 신고자가 낙찰자면 주최자 신고, 주최자면 낙찰자 신고
        User user = (ticketReport.getTicketReportNickname().equals(winner.getNickname())) ?
                organizer : winner;

        // 유저의 경고 내역에 추가
        Warning warning = Warning.builder()
                .user(user)
                .warningContent(ticketReport.getTicketReportContent())
                .build();

        warningRepository.save(warning);

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

    @Transactional
    public void refusalTicketReport(String ticketReportUUID) {
        TicketReport ticketReport = ticketReportRepository.findByTicketReportUUID(ticketReportUUID)
                .orElseThrow(() -> new CustomException(ErrorCode.TICKET_REPORT_NOT_FOUND));

        if (ticketReport.getTicketReportStatus() != TicketReportStatus.STANDBY) {
            throw new CustomException(ErrorCode.TICKET_REPORT_ALREADY_HANDLED);
        }

        ticketReport.changeTicketReportStatus(TicketReportStatus.REFUSAL);
        ticketReportRepository.save(ticketReport);
    }

}
