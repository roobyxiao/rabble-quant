package com.whzz.web;

import cn.hutool.core.date.DateUtil;
import com.whzz.dao.TickDao;
import com.whzz.pojo.Tick;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/tick")
public class TickController {

    @Autowired
    private TickDao tickDao;

    @RequestMapping(value = "/save")
    public String saveTick(@RequestBody Tick tick)
    {
        tickDao.save(tick);
        return "tick saved: " + DateUtil.now();
    }
}
