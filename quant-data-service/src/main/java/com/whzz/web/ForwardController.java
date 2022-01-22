package com.whzz.web;

import cn.hutool.core.date.DateUtil;
import com.whzz.dao.ForwardDao;
import com.whzz.pojo.Forward;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/forward")
public class ForwardController {

    @Autowired
    private ForwardDao forwardDao;

    @RequestMapping(value = "/saveAll")
    public String saveForwards(@RequestBody List<Forward> forwards)
    {
        forwardDao.saveAll(forwards);
        return "forwards saved: " + DateUtil.now();
    }

    @GetMapping(value = "/get/{code}")
    public List<Forward> getByCode(@PathVariable("code") String code)
    {
        return forwardDao.findByCodeOrderByDateDesc(code);
    }

    @RequestMapping(value = "/delete/{code}")
    public String deleteByCode(@PathVariable("code") String code)
    {
        List<Forward> forwards = forwardDao.findByCodeOrderByDateDesc(code);
        forwardDao.deleteAll(forwards);
        return "forwards deleted: " + DateUtil.now();
    }
}
