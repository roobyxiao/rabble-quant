package com.whzz.web;

import cn.hutool.core.date.DateUtil;
import com.whzz.dao.DividendDao;
import com.whzz.pojo.Dividend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/dividend")
public class DividendController {

    @Autowired
    private DividendDao dividendDao;

    @RequestMapping(value = "/saveAll")
    public String saveDividends(@RequestBody List<Dividend> dividends)
    {
        dividendDao.saveAll(dividends);
        return "dividends saved: " + DateUtil.now();
    }

    @RequestMapping(value = "/save")
    public String saveDividend(@RequestBody Dividend dividend)
    {
        dividendDao.save(dividend);
        return "dividend saved: " + DateUtil.now();
    }

    @GetMapping(value = "/getCodes")
    public List<String> findDistinctCodes()
    {
        return dividendDao.findDistinctCodes();
    }

    @GetMapping(value = "/get/{code}")
    public List<Dividend> getByCode(@PathVariable("code") String code)
    {
        return dividendDao.findByCodeOrderByDividendDateDesc(code);
    }
}
