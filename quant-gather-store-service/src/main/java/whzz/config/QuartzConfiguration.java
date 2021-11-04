package whzz.config;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import whzz.job.QuantSpiderJob;

@Configuration
public class QuartzConfiguration {
    private static final int interval=1;
    @Bean
    public JobDetail jobDetail(){
        return JobBuilder.newJob(QuantSpiderJob.class).withIdentity("quantSyncJob").storeDurably().build();
    }
    @Bean
    public Trigger trigger(){
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0 0 17 * * ?");
        return TriggerBuilder.newTrigger().forJob(jobDetail())
                .withIdentity("quantSyncTrigger").withSchedule(cronScheduleBuilder).build();
    }
}
