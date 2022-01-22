package com.whzz.web;

import cn.hutool.core.date.DateUtil;
import com.whzz.dao.DailyDao;
import com.whzz.dao.StockDao;
import com.whzz.dao.UpLimitDao;
import com.whzz.pojo.UpLimit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/limit")
public class UpLimitController {

    @Autowired
    private UpLimitDao upLimitDao;

    @Autowired
    private DailyDao dailyDao;

    @Autowired
    private StockDao stockDao;

    @RequestMapping(value = "/saveAll")
    public String saveLimits(@RequestBody List<UpLimit> limits)
    {
        upLimitDao.saveAll(limits);
        return "limits saved: " + DateUtil.now();
    }

    @RequestMapping(value = "/save")
    public String saveLimit(@RequestBody UpLimit limit)
    {
        upLimitDao.save(limit);
        return "limit saved: " + DateUtil.now();
    }

    @GetMapping(value = "/getAll")
    public List<UpLimit> getAllLimits()
    {
        return upLimitDao.findAll();
    }

    @GetMapping(value = "/getLimitsByDate/{date}")
    public List<UpLimit> getLimitsByDate(@PathVariable("date") Date date)
    {
        return upLimitDao.findUpLimitsByDate(date);
    }
}
