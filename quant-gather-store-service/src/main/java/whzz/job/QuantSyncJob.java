package whzz.job;

import cn.hutool.core.date.DateUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import whzz.pojo.Calendar;
import whzz.pojo.Stock;
import whzz.service.*;

import java.util.List;

public class QuantSyncJob extends QuartzJobBean {
    @Autowired
    private StockService stockService;
    @Autowired
    private CalendarService calendarService;
    @Autowired
    private DividendService dividendService;
    @Autowired
    private DailyService dailyService;
    @Autowired
    private ForwardService forwardService;
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        System.out.println("定时启动：" + DateUtil.now());
        List<Calendar> calendars = calendarService.fresh();
        List<Stock> stocks = stockService.fresh();
        for(Stock stock : stocks) {
            dailyService.fresh(stock.getCode());
        }
    }
}
