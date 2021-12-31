package whzz.config;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import whzz.job.ProcessingJob;

@Configuration
public class QuartzConfiguration {
    private static final int interval=1;
    @Bean
    public JobDetail jobDetail(){
        return JobBuilder.newJob(ProcessingJob.class).withIdentity("ProcessingJob").storeDurably().build();
    }
    @Bean
    public Trigger trigger(){
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMinutes(interval).withRepeatCount(0);
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0 0 18 * * ?");
        return TriggerBuilder.newTrigger().forJob(jobDetail())
                .withIdentity("ProcessingTrigger").withSchedule(scheduleBuilder).build();
    }
}