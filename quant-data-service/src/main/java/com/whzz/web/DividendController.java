package com.whzz.web;

import cn.hutool.core.date.DateUtil;
import com.whzz.dao.DividendDao;
import com.whzz.pojo.Dividend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
