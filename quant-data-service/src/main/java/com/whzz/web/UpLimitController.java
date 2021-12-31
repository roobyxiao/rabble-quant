package com.whzz.web;

import cn.hutool.core.date.DateUtil;
import com.whzz.dao.UpLimitDao;
import com.whzz.pojo.UpLimit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/limit")
public class UpLimitController {

    @Autowired
    private UpLimitDao upLimitDao;

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

    @RequestMapping(value = "/getAll")
    public List<UpLimit> getAllLimits()
    {
        return upLimitDao.findAll();
    }

    @RequestMapping(value = "/getLimitsByDate/{date}")
    public List<UpLimit> getLimitsByDate(@PathVariable("date") Date date)
    {
        return upLimitDao.findUpLimitsByDate(date);
    }
}
