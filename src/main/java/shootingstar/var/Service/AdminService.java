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
import shootingstar.var.jwt.JwtTokenProvider;
import shootingstar.var.jwt.TokenInfo;
import shootingstar.var.repository.BanRepository;
import shootingstar.var.repository.admin.AdminRepository;
import shootingstar.var.repository.exchange.ExchangeRepository;
import shootingstar.var.repository.review.ReviewRepository;
import shootingstar.var.repository.ticket.TicketRepository;
import shootingstar.var.repository.user.UserRepository;
import shootingstar.var.repository.vip.VipInfoRepository;

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

    @Value("${admin-secret-signup-key}")
    private String adminSignupSecretKey;

    public void signup(String id, String password, String nickname, String secretKey) {
        if (!secretKey.equals(adminSignupSecretKey)) {
            throw new RuntimeException("회원가입 실패 잘못된 인증 키");
        }
        if (adminRepository.existsByAdminLoginId(id)) {
            throw new RuntimeException("해당 id로 가입 불가능");
        }
        if (adminRepository.existsByNickname(nickname)) {
            throw new RuntimeException("해당 닉네임으로 가입 불가능");
        }
        String encodePassword = passwordEncoder.encode(password); // 패스워드 암호화
        Admin admin = new Admin(id, encodePassword, nickname);
        adminRepository.save(admin);
    }

    public TokenInfo login(String id, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(id, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        return tokenProvider.generateToken(authentication);
    }

    public Page<AllUsersDto> getAllUsers(String search, Pageable pageable) {
        return userRepository.findAllUsers(search, pageable);
    }

    @Transactional
    public void warning(String userUUID) {
        User user = userRepository.findByUserUUID(userUUID)
                .orElseThrow(RuntimeException::new);

        if (user.getWarningCount() >= 3) {
            // 경고 횟수가 이미 3인 유저
            throw new RuntimeException("이미 밴당한 유저");
        }

        // 경고 횟수 + 1
        user.setWarningCount(user.getWarningCount() + 1);
        // 변경된 유저 경고 횟수 저장
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
                .orElseThrow(RuntimeException::new);

        return new AllVipInfosDto(
                vipInfo.getVipInfoUUID(),
                vipInfo.getVipName(),
                vipInfo.getVipJob(),
                vipInfo.getVipCareer(),
                vipInfo.getVipIntroduce()
        );
    }

    @Transactional
    public void vipInfoChange(String vipInfoUUID, String vipInfoState) {
        VipInfo vipInfo = vipInfoRepository.findByVipInfoUUID(vipInfoUUID)
                .orElseThrow(RuntimeException::new);
        if (vipInfoState.equalsIgnoreCase("APPROVE")) {
            vipInfo.changeVipApproval(VipApprovalType.APPROVE);
        }
        else if (vipInfoState.equalsIgnoreCase("REFUSAL")) {
            vipInfo.changeVipApproval(VipApprovalType.REFUSAL);
        }
        else {
            throw new RuntimeException("잘못된 형식(APPROVE or REFUSAL");
        }
        vipInfoRepository.save(vipInfo);
    }

    public Page<AllTicketsDto> getAllTickets(String search, Pageable pageable) {
        return ticketRepository.findAllTickets(search, pageable);
    }

    public Page<AllReviewsDto> getAllReviews(String search, Pageable pageable) {
        return reviewRepository.findAllReviews(search, pageable);
    }

    public Page<AllExchangesDto> getAllExchanges(String search, Pageable pageable) {
        return exchangeRepository.findAllExchanges(search, pageable);
    }

}
