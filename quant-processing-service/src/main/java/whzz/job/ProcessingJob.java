package whzz.job;

import cn.hutool.core.date.DateUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import whzz.service.QuantProcessingService;

public class ProcessingJob extends QuartzJobBean {

    @Autowired
    private QuantProcessingService quantProcessingService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        System.out.println("定时启动：" + DateUtil.now());
        //String startDate = "2019-01-01";
        //String startDate = new Date(new java.util.Date().getTime()).toString();
        String startDate = "2021-12-06";
        quantProcessingService.processUpLimit();
    }
}
