package com.whzz.web;

import cn.hutool.core.date.DateUtil;
import com.whzz.dao.StockDao;
import com.whzz.pojo.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/stock")
public class StockController {
    @Autowired
    private StockDao stockDao;

    @RequestMapping(value = "/save")
    public String saveStock(@RequestBody Stock stock)
    {
        stockDao.save(stock);
        return "stocks saved: " + stock.getCode() + "---" + DateUtil.now();
    }

    @RequestMapping(value = "/saveAll")
    public String saveStocks(@RequestBody List<Stock> stocks)
    {
        stockDao.saveAll(stocks);
        return "stocks saved: " + DateUtil.now();
    }

    @GetMapping("get/{code}")
    public Optional<Stock> getStock(@PathVariable("code")String code)
    {
        return stockDao.findById(code);
    }

    @GetMapping("/exists/{code}")
    public boolean stockExists(@PathVariable("code")String code)
    {
        return stockDao.existsById(code);
    }

    @GetMapping("/getAll")
    public List<Stock> getAllStocks()
    {
        return stockDao.findAll();
    }

    @GetMapping("/getListStocks")
    public List<Stock> getListStocks()
    {
        return stockDao.findAllByStatus(true);
    }
}
