package whzz.task;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import whzz.service.QuoteService;

import java.sql.Date;

@Component
@Slf4j
public class QuoteTask {

    @Autowired
    private QuoteService quoteService;

    @Scheduled(cron = "0 0 9 * * ?")
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 5000L, multiplier = 2.0))
    public void quoteBeforeOpen() {
        String date = new Date(new java.util.Date().getTime()).toString();
        if (quoteService.isOpen(date)) {
            log.info("定时启动：" + DateUtil.now());
            quoteService.restoreCalendar(date);
            quoteService.restoreStock();
            quoteService.validateStock();
            quoteService.restoreDividendByDate(date);
        }
    }

    @Scheduled(cron = "0 30 16 * * ?")
    @Retryable(value = NullPointerException.class, maxAttempts = 3, backoff = @Backoff(delay = 5000L, multiplier = 2.0))
    public void quoteAfterClose() {
        String date = new Date(new java.util.Date().getTime()).toString();
        if (quoteService.isOpen(date)) {
            log.info("定时启动：" + DateUtil.now());
            quoteService.restoreEmDailyByDate(date);
            quoteService.restoreEastMoneyLimit(date);
            quoteService.restoreTickByDate(date);
        }
    }
}
