package com.whzz.web;

import cn.hutool.core.date.DateUtil;
import com.whzz.dao.DailyDao;
import com.whzz.pojo.Daily;
import com.whzz.pojo.DailyId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/daily")
public class DailyController {

    @Autowired
    private DailyDao dailyDao;

    @GetMapping(value="/get/{code}/{date}")
    public Daily getDailyById(@PathVariable("code") String code, @PathVariable("date") String date)
    {
        DailyId dailyId = new DailyId(code, Date.valueOf(date));
        return dailyDao.findById(dailyId).get();
    }

    @RequestMapping(value = "/saveAll")
    public String saveDailies(@RequestBody List<Daily> dailies)
    {
        dailyDao.saveAll(dailies);
        return "dailies saved: " + DateUtil.now();
    }

    @Transactional
    @RequestMapping(value = "/updateDailyLimits")
    public String updateDailyLimits(@RequestBody List<Daily> dailies)
    {
        dailies.forEach(daily -> {
            dailyDao.updateDaiyLimits(daily.getLimitUp(), daily.getLimitDown(), daily.getCode(), daily.getDate());
        });
        return "dailies updated: " + DateUtil.now();
    }
}
