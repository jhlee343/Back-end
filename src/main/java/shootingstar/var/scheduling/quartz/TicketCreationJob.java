package shootingstar.var.scheduling.quartz;

import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import shootingstar.var.Service.SchedulerService;

@RequiredArgsConstructor
public class TicketCreationJob implements Job {
    private final SchedulerService schedulerService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        long auctionId = context.getJobDetail().getJobDataMap().getLong("auctionId");
        long userId = context.getJobDetail().getJobDataMap().getLong("userId");
        long scheduledTaskId = context.getJobDetail().getJobDataMap().getLong("scheduledTaskId");

        schedulerService.createTicketAndAuctionTypeSuccess(auctionId, userId, scheduledTaskId);
    }
}
