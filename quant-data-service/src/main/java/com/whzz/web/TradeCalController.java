package com.whzz.web;

import cn.hutool.core.date.DateUtil;
import com.whzz.dao.TradeCalDao;
import com.whzz.pojo.TradeCal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/cal")
public class TradeCalController {
    @Autowired
    private TradeCalDao tradeCalDao;

    @RequestMapping(value = "/saveAll")
    public String saveTradeCals(@RequestBody List<TradeCal> cals)
    {
        tradeCalDao.saveAll(cals);
        return "calendars saved: " + DateUtil.now();
    }

    @RequestMapping(value = "/getMax")
    public String getMaxDate()
    {
        TradeCal cal = tradeCalDao.findTopByOrderByDateDesc();
        if (cal == null)
            return "2019-01-01";
        return cal.getDate().toString();
    }

    @GetMapping(value = "/getOpenCals/{date}")
    public @ResponseBody List<TradeCal> getOpenCals(@PathVariable("date") String date)
    {
        Date startDate = Date.valueOf(date);
        List<TradeCal> cals = tradeCalDao.findAllByDateBetweenAndOpenIsTrue(startDate, new Date(System.currentTimeMillis()));
        return cals;
    }

    @GetMapping(value = "/isOpen/{date}")
    public boolean isOpenCal(@PathVariable("date") String date)
    {
        Date calDate = Date.valueOf(date);
        TradeCal cal = tradeCalDao.findTradeCalByDate(calDate);
        return cal.isOpen();
    }
}
