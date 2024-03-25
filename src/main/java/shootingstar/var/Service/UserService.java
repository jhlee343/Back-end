package shootingstar.var.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import shootingstar.var.dto.req.FollowingDto;
import shootingstar.var.dto.req.UserProfileDto;
import shootingstar.var.dto.req.UserSignupReqDto;
import org.springframework.transaction.annotation.Transactional;
import shootingstar.var.dto.req.WarningListDto;
import shootingstar.var.dto.res.UserReceiveReviewDto;
import shootingstar.var.dto.res.UserSendReviewDto;
import shootingstar.var.entity.*;
import shootingstar.var.exception.CustomException;
import shootingstar.var.exception.ErrorCode;
import shootingstar.var.jwt.JwtTokenProvider;
import shootingstar.var.repository.FollowRepository;
import shootingstar.var.repository.Review.ReviewRepository;
import shootingstar.var.repository.ReviewReport.ReviewReportRepository;
import shootingstar.var.repository.UserRepository;
import shootingstar.var.repository.Warning.WarningRepository;
import shootingstar.var.util.MailRedisUtil;

import static shootingstar.var.exception.ErrorCode.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final MailRedisUtil mailRedisUtil;
    private final CheckDuplicateService duplicateService;
    private final WarningRepository warningRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewReportRepository reviewReportRepository;

    public void signup(UserSignupReqDto reqDto) {
        if (duplicateService.checkEmailDuplicate(reqDto.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (duplicateService.checkNicknameDuplicate(reqDto.getNickname())) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        if (mailRedisUtil.hasKey(reqDto.getEmail()) && mailRedisUtil.getData(reqDto.getEmail()).equals("validate")) { // 이메일 인증을 받은 이메일 인지 확인

            User user = User.builder()
                    .kakaoId(reqDto.getKakaoId())
                    .name(reqDto.getUserName())
                    .nickname(reqDto.getNickname())
                    .phone(reqDto.getPhoneNumber())
                    .email(reqDto.getEmail())
                    .profileImgUrl(reqDto.getProfileImgUrl())
                    .userType(UserType.ROLE_BASIC)
                    .build();

            userRepository.save(user);
            mailRedisUtil.deleteData(reqDto.getEmail());
        } else {
            throw new CustomException(ErrorCode.VALIDATE_ERROR_EMAIL);
        }
    }

    public boolean checkVIP(HttpServletRequest request) {
        String userUUID = jwtTokenProvider.getUserUUIDByRequest(request);
        User user = findByUserUUID(userUUID);
        if (user.getUserType().equals(UserType.ROLE_VIP)) {
            //vip인 경우 true
            return true;
        } else {
            //vip가 아닌경우 false
            return false;
        }
    }

    public UserProfileDto getProfile(String nickname) {
        User user = findByNickname(nickname);
        UserProfileDto userProfileDto = new UserProfileDto(user.getNickname(), user.getProfileImgUrl(), user.getDonationPrice(), user.getPoint(), user.getSubscribe(), user.getUserType());
        return userProfileDto;
    }

    public List<FollowingDto> findAllFollowing(String userUUID) {
        return followRepository.findAllByFollowerId(userUUID);
    }

    @Transactional
    public void follow(String followingId, String userUUID) {
        User follower = findByUserUUID(userUUID);
        User following = findByUserUUID(followingId);
        UUID followUUID = UUID.randomUUID();
        Follow follow = new Follow(follower,following);
        followRepository.save(follow);
    }

    @Transactional
    public void unFollow(String followUUID) {
        Follow follow = findFollowingByFollowUUID(followUUID);
        followRepository.delete(follow);
    }

    public Page<UserReceiveReviewDto> receiveReview(String userUUID, Pageable pageable){
        return reviewRepository.findAllReviewByuserUUID(userUUID,pageable);
    }

    public Page<UserSendReviewDto> sendReview(String userUUID, Pageable pageable){
        return reviewRepository.findAllSendByuserUUID(userUUID,pageable);
    }
    public List<WarningListDto> findAllWarning(String userUUID) {
        return warningRepository.findAllWarnByUserUUID(userUUID);
    }

    @Transactional
    public void reportReview(Long reviewId){
        Review review = findByReviewId(reviewId);
        ReviewReport reviewReport = new ReviewReport(
                review,
                review.getReviewContent(),
                ReviewReportStatus.STANDBY
        );
        reviewReportRepository.save(reviewReport);
    }

    private Follow findFollowingByFollowUUID(String followUUID) {
        Optional<Follow> followOptional = followRepository.findByFollowUUID(followUUID);
        if (followOptional.isEmpty()) {
            throw new CustomException(USER_NOT_FOUND);
        }
        return followOptional.get();
    }

    public User findByNickname(String nickname) {
        Optional<User> optionalUser = userRepository.findByNickname(nickname);
        if (optionalUser.isEmpty()) {
            throw new CustomException(USER_NOT_FOUND);
        }
        return optionalUser.get();
    }

    public User findByUserUUID(String userUUID) {
        Optional<User> optionalUser = userRepository.findByUserUUID(userUUID);
        if (optionalUser.isEmpty()) {
            throw new CustomException(USER_NOT_FOUND);
        }
        return optionalUser.get();
    }

    public Review findByReviewId(Long reviewId){
        Optional<Review> optionalReview = reviewRepository.findByReviewId(reviewId);
        if(optionalReview.isEmpty()){
            throw new CustomException(REVIEW_NOT_FOUND);
        }
        return optionalReview.get();
    }

}