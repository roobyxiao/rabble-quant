package com.whzz.web;

import cn.hutool.core.date.DateUtil;
import com.whzz.dao.DailyDao;
import com.whzz.dao.DividendDao;
import com.whzz.dao.StockDao;
import com.whzz.dao.TradeCalDao;
import com.whzz.dao.UpLimitDao;
import com.whzz.pojo.Daily;
import com.whzz.pojo.Dividend;
import com.whzz.pojo.Stock;
import com.whzz.pojo.TradeCal;
import com.whzz.pojo.UpLimit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class QuantDataController {

    @Autowired
    private StockDao stockDao;
    @Autowired
    private TradeCalDao tradeCalDao;
    @Autowired
    private DailyDao dailyDao;
    @Autowired
    private UpLimitDao upLimitDao;
    @Autowired
    private DividendDao dividendDao;

    @RequestMapping(value = "/stock/saveAll")
    public String saveStocks(@RequestBody List<Stock> stocks)
    {
        stockDao.saveAll(stocks);
        return "stocks saved: " + DateUtil.now();
    }

    @GetMapping("/stock/{code}")
    public Optional<Stock> get(@PathVariable("code")String code)
    {
        return stockDao.findById(code);
    }

    @GetMapping("/stock/get")
    public List<Stock> get()
    {
        return stockDao.findAll();
    }

    @RequestMapping(value = "/cal/saveAll")
    public String saveTradeCals(@RequestBody List<TradeCal> cals)
    {
        tradeCalDao.saveAll(cals);
        return "calendars saved: " + DateUtil.now();
    }

    @RequestMapping(value = "/cal/max")
    public String getMaxDate()
    {
        TradeCal cal = tradeCalDao.findTopByOrderByDateDesc();
        if (cal == null)
            return "2019-01-01";
        return cal.getDate().toString();
    }

    @GetMapping(value = "/cal/open/{date}")
    public @ResponseBody List<TradeCal> getOpenCals(@PathVariable("date") String date)
    {
        Date startDate = Date.valueOf(date);
        List<TradeCal> cals = tradeCalDao.findAllByDateBetweenAndOpenIsTrue(startDate, new Date(System.currentTimeMillis()));
        return cals;
    }

    @RequestMapping(value = "/daily/saveAll")
    public String saveDailies(@RequestBody List<Daily> dailies)
    {
        dailyDao.saveAll(dailies);
        return "dailies saved: " + DateUtil.now();
    }

    @RequestMapping(value = "/daily/updateLimits")
    public String updateDailyLimits(@RequestBody List<Daily> dailies)
    {
        dailies.forEach(daily -> dailyDao.updateDaiyLimits(daily.getHighLimit(), daily.getLowLimit(), daily.getCode(), daily.getDate()));
        return "dailies updated: " + DateUtil.now();
    }

    @RequestMapping(value = "/limit/saveAll")
    public String saveLimits(@RequestBody List<UpLimit> limits)
    {
        upLimitDao.saveAll(limits);
        return "limits saved: " + DateUtil.now();
    }

    @RequestMapping(value = "/dividend/saveAll")
    public String saveDividends(@RequestBody List<Dividend> dividends)
    {
        dividendDao.saveAll(dividends);
        return "dividends saved: " + DateUtil.now();
    }
}
