package shootingstar.var.entity.log;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shootingstar.var.entity.BaseTimeEntity;
import shootingstar.var.entity.User;
import shootingstar.var.enums.type.PointOriginType;
import shootingstar.var.enums.type.TransactionType;

@Entity
@Getter
@NoArgsConstructor
public class PointLog extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointLogId;

    private String pointLogUUID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    private PointOriginType pointOriginType;

    @Column(precision = 10, scale = 2)
    private BigDecimal point;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalUserPoint;

    @Builder
    public PointLog(User user, TransactionType transactionType, PointOriginType pointOriginType,
                    BigDecimal point, BigDecimal totalUserPoint) {
        this.pointLogUUID = UUID.randomUUID().toString();
        this.user = user;
        this.transactionType = transactionType;
        this.pointOriginType = pointOriginType;
        this.point = point;
        this.totalUserPoint = totalUserPoint;
    }

    public static PointLog createPointLogWithDeposit(User user, PointOriginType pointOriginType, BigDecimal point) {
        PointLog pointLog = PointLog.builder()
                .user(user)
                .transactionType(TransactionType.DEPOSIT)
                .pointOriginType(pointOriginType)
                .point(point)
                .totalUserPoint(user.getPoint())
                .build();
        return pointLog;
    }

    public static PointLog createPointLogWithWithdrawal(User user, PointOriginType pointOriginType, BigDecimal point) {
        PointLog pointLog = PointLog.builder()
                .user(user)
                .transactionType(TransactionType.WITHDRAWAL)
                .pointOriginType(pointOriginType)
                .point(point)
                .totalUserPoint(user.getPoint())
                .build();
        return pointLog;
    }
}
