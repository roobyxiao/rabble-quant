package whzz.config;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import whzz.job.QuantSpiderJob;

@Configuration
public class QuartzConfiguration {
    private static final int interval=1;
    @Bean
    public JobDetail weatherDataSyncJobDetail(){
        return JobBuilder.newJob(QuantSpiderJob.class).withIdentity("quantSyncJob").storeDurably().build();
    }
    @Bean
    public Trigger weatherDataSyncTrigger(){
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMinutes(interval).withRepeatCount(0);
        return TriggerBuilder.newTrigger().forJob(weatherDataSyncJobDetail())
                .withIdentity("quantSyncTrigger").withSchedule(scheduleBuilder).build();
    }
}
