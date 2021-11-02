package whzz.job;

import cn.hutool.core.date.DateUtil;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import whzz.service.*;

import java.text.ParseException;
import java.util.Date;

public class QuantSpiderJob extends QuartzJobBean {
    @Autowired
    private QuantSpiderService quantSpiderService;
    @Override
    protected void executeInternal(JobExecutionContext context)
    {
        System.out.println("定时启动：" + DateUtil.now());
        /*quantSpiderService.restoreStock();
        quantSpiderService.restoreCalendar();
        //quantSpiderService.restoreDaily();
        try {
            quantSpiderService.restoreLimit();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }*/
        try {
            //quantSpiderService.restoreUpLimit();
            quantSpiderService.restoreEastMoneyLimit(null);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        /*quantSpiderService.restoreDividend("2017-06-30");
        quantSpiderService.restoreDividend("2017-12-31");
        quantSpiderService.restoreDividend("2018-06-30");
        quantSpiderService.restoreDividend("2018-12-31");
        quantSpiderService.restoreDividend("2019-06-30");
        quantSpiderService.restoreDividend("2019-12-31");
        quantSpiderService.restoreDividend("2020-06-30");
        quantSpiderService.restoreDividend("2020-12-31");
        quantSpiderService.restoreDividend("2021-06-30");
        quantSpiderService.restoreDividend("2021-12-31");*/
    }
}
