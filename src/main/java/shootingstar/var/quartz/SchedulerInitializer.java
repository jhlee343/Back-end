package shootingstar.var.quartz;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import shootingstar.var.Service.SchedulerService;
import shootingstar.var.entity.ScheduledTask;
import shootingstar.var.entity.TaskType;
import shootingstar.var.exception.CustomException;
import shootingstar.var.exception.ErrorCode;
import shootingstar.var.repository.ScheduledTaskRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerInitializer{
    private final ScheduledTaskRepository scheduledTaskRepository;
    private final SchedulerService schedulerService;
    private final Scheduler quartzScheduler;

    @EventListener(ContextRefreshedEvent.class)
    public void initializeScheduledTasks() {
        // 아직 실행되지 않은 모든 작업 조회
        List<ScheduledTask> tasks = scheduledTaskRepository.findByTaskType(TaskType.STANDBY);

        for (ScheduledTask task : tasks) {
            LocalDateTime scheduledTime = task.getScheduledTime();
            Instant instant = scheduledTime.atZone(ZoneId.systemDefault()).toInstant();

            // 예정된 시간이 현재 시간보다 이후일 경우에만 스케줄링
            if (instant.isAfter(Instant.now())) {
                JobDetail jobDetail = JobBuilder.newJob(TicketCreationJob.class)
                        .withIdentity(UUID.randomUUID().toString(), "ticket-creation-jobs")
                        .usingJobData("auctionId", task.getAuctionId())
                        .usingJobData("userId", task.getUserId())
                        .usingJobData("taskId", task.getScheduledTaskId())
                        .build();

                Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity(jobDetail.getKey().getName(), "ticket-creation-triggers")
                        .startAt(Date.from(instant))
                        .build();

                try {
                    log.info("db에 있던 스케줄링 작업 추가");
                    quartzScheduler.scheduleJob(jobDetail, trigger);
                } catch (SchedulerException e) {
                    log.info("스케줄링 초기화 에러 발생", e);
                    throw new CustomException(ErrorCode.SCHEDULING_SERVER_ERROR);
                }
            } else {
                schedulerService.createTicketAndAuctionTypeSuccess(task.getAuctionId(), task.getUserId(), task.getScheduledTaskId());
            }
        }
    }

}
