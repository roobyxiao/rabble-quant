package whzz.job;

import cn.hutool.core.date.DateUtil;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import whzz.client.QuantDataClient;
import whzz.service.QuantSpiderService;

import java.sql.Date;

public class QuantSpiderJob extends QuartzJobBean {
    @Autowired
    private QuantSpiderService quantSpiderService;
    @Autowired
    private QuantDataClient quantDataClient;
    @Override
    protected void executeInternal(JobExecutionContext context)
    {
        String startDate = new Date(new java.util.Date().getTime()).toString();
        syncDaily(startDate);
    }

    private void syncAll()
    {
        System.out.println("定时启动：" + DateUtil.now());
        String startDate = "2019-01-01";
        quantSpiderService.restoreCalendar(startDate);
        quantSpiderService.restoreStock();
        quantSpiderService.restoreDailyByStock(startDate);
        quantSpiderService.restoreLimit(startDate);
        quantSpiderService.restoreEastMoneyLimit("");
        int startYear = Integer.valueOf(startDate.split("-")[0]) - 2;
        quantSpiderService.restoreDividend(startYear);
    }

    private void syncDaily(String startDate)
    {
        System.out.println("定时启动：" + DateUtil.now());
        quantSpiderService.restoreCalendar(startDate);
        quantSpiderService.restoreStock();
        quantSpiderService.restoreEmDailyByDate(startDate);
        quantSpiderService.restoreEastMoneyLimit(startDate);
        int startYear = Integer.valueOf(startDate.split("-")[0]) - 2;
        quantSpiderService.restoreDividend(startYear);
        quantSpiderService.restoreTickByDate(startDate);
    }

    private boolean isOpen(String date)
    {
        return quantDataClient.isOpen(date);
    }

}
