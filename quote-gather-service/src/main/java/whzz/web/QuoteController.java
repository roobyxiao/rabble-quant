package whzz.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import whzz.service.QuoteService;

import java.sql.Date;

@Slf4j
@RestController
@RequestMapping(value = "/quote")
public class QuoteController {

    @Autowired
    private QuoteService quoteService;

    @RequestMapping(value = "/all")
    public void updateAll()
    {
        String startDate = "2019-01-01";
        String limitStartDate = "2019-12-02";
        /*quoteService.restoreCalendar(startDate);
        quoteService.restoreStock();
        quoteService.validateStock();
        quoteService.restoreDaily(startDate);
        quoteService.restoreTushareLimit(startDate);
        quoteService.restoreForward(startDate);
        quoteService.restoreEastMoneyLimit(limitStartDate);
        quoteService.restoreDividend(startDate);*/
        quoteService.restoreForward(startDate);
        quoteService.updateForwards();
    }

    @RequestMapping(value = "/daily")
    public void updateDaily()
    {
        String date = new Date(new java.util.Date().getTime()).toString();
        if (quoteService.isOpen(date)) {
            quoteService.restoreCalendar(date);
            quoteService.restoreStock();
            quoteService.validateStock();
            int count1 = quoteService.restoreEmDailyByDate(date);
            quoteService.restoreEastMoneyLimit(date);
            quoteService.restoreDividendByDate(date);
            int count2 = quoteService.restoreTickByDate(date);
            assert count1 == count2;
        }
    }
}
