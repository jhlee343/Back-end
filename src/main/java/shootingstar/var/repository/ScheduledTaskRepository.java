package shootingstar.var.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import shootingstar.var.entity.ScheduledTask;
import shootingstar.var.entity.TaskType;

public interface ScheduledTaskRepository extends JpaRepository<ScheduledTask, Long> {
    List<ScheduledTask> findByTaskType(TaskType taskType);

    Optional<ScheduledTask> findByAuctionId(Long auctionId);
}
