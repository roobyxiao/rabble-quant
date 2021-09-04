package whzz.job;

import cn.hutool.core.date.DateUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import whzz.service.QuantSpiderService;

import java.text.ParseException;

public class QuantSpiderJob extends QuartzJobBean {
    @Autowired
    private QuantSpiderService quantSpiderService;
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        System.out.println("定时启动：" + DateUtil.now());
        try {
            quantSpiderService.restoreDividend("2018-06-30");
            quantSpiderService.restoreDividend("2018-12-31");
            quantSpiderService.restoreDividend("2019-06-30");
            quantSpiderService.restoreDividend("2019-12-31");
            quantSpiderService.restoreDividend("2020-06-30");
            quantSpiderService.restoreDividend("2020-12-31");
            quantSpiderService.restoreDividend("2021-06-30");
            quantSpiderService.restoreDividend("2021-12-31");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
