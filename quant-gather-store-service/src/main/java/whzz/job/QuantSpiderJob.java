package whzz.job;

import cn.hutool.core.date.DateUtil;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import whzz.service.QuantSpiderService;

public class QuantSpiderJob extends QuartzJobBean {
    @Autowired
    private QuantSpiderService quantSpiderService;
    @Override
    protected void executeInternal(JobExecutionContext context)
    {
        System.out.println("定时启动：" + DateUtil.now());
        quantSpiderService.restoreStock();
        /*Date startDate = new Date(new java.util.Date().getTime());
        quantSpiderService.restoreCalendar();
        quantSpiderService.restoreDaily(startDate);
        try {
            quantSpiderService.restoreLimit(startDate);
            quantSpiderService.restoreEastMoneyLimit(startDate);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }*/
        //quantSpiderService.restoreDividend(2018);
    }
}
