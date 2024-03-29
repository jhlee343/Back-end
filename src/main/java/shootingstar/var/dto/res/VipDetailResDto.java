package shootingstar.var.dto.res;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.util.List;

@Data
public class VipDetailResDto {
    private String vipUserUUID;
    private String vipProfileImgUrl;
    private String vipNickname;
    private String vipRating;
    private boolean isFollow;
    private String vipJop;
    private String vipCareer;
    private String vipIntroduce;
    private List<VipProgressAuctionResDto> progressAuctionList;
    private List<VipReceiveReviewResDto> receiveReviewList;

    @QueryProjection
    public VipDetailResDto(String vipUserUUID, String vipProfileImgUrl, String vipNickname, String vipRating, boolean isFollow, String vipJop, String vipCareer, String vipIntroduce, List<VipProgressAuctionResDto> progressAuctionList, List<VipReceiveReviewResDto> receiveReviewList) {
        this.vipUserUUID = vipUserUUID;
        this.vipProfileImgUrl = vipProfileImgUrl;
        this.vipNickname = vipNickname;
        this.vipRating = vipRating;
        this.isFollow = isFollow;
        this.vipJop = vipJop;
        this.vipCareer = vipCareer;
        this.vipIntroduce = vipIntroduce;
        this.progressAuctionList = progressAuctionList;
        this.receiveReviewList = receiveReviewList;
    }
}
