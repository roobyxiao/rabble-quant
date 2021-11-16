package whzz.job;

import cn.hutool.core.date.DateUtil;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import whzz.service.QuantSpiderService;
import java.sql.Date;

public class QuantSpiderJob extends QuartzJobBean {
    @Autowired
    private QuantSpiderService quantSpiderService;
    @Override
    protected void executeInternal(JobExecutionContext context)
    {
        System.out.println("定时启动：" + DateUtil.now());
        String startDate = "2019-01-01";
        //Date startDate = new Date(new java.util.Date().getTime());
        //quantSpiderService.restoreCalendar(startDate);
        //quantSpiderService.restoreStock();

        //quantSpiderService.restoreDailyByStock(startDate);
        //quantSpiderService.restoreLimit(startDate);

        //quantSpiderService.restoreEastMoneyLimit("");
        quantSpiderService.restoreDividend(2018);
    }
}
