package shootingstar.var.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shootingstar.var.enums.type.TaskType;

@Entity
@Getter
@NoArgsConstructor
public class ScheduledTask extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduledTaskId;

    private Long auctionId;
    private Long userId;
    private LocalDateTime scheduledTime;

    @Enumerated(value = EnumType.STRING)
    private TaskType taskType;

    @Builder
    public ScheduledTask(Long auctionId, Long userId, LocalDateTime scheduledTime) {
        this.auctionId = auctionId;
        this.userId = userId;
        this.scheduledTime = scheduledTime;
        this.taskType = TaskType.STANDBY;
    }

    public void changeTaskType(TaskType taskType) {
        this.taskType = taskType;
    }
}
