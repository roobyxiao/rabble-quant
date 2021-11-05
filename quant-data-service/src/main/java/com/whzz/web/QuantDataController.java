package com.whzz.web;

import cn.hutool.core.date.DateUtil;
import com.whzz.dao.StockRepository;
import com.whzz.dao.TradeCalRepository;
import com.whzz.pojo.Stock;
import com.whzz.pojo.TradeCal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class QuantDataController {

    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private TradeCalRepository tradeCalRepository;

    @RequestMapping(value = "/stock/saveAll")
    public String saveStocks(@RequestBody List<Stock> stocks)
    {
        stockRepository.saveAll(stocks);
        return "stocks saved: " + DateUtil.now();
    }

    @GetMapping("/stock/{code}")
    public Optional<Stock> get(@PathVariable("code")String code)
    {
        return stockRepository.findById(code);
    }

    @GetMapping("/stock/get")
    public List<Stock> get()
    {
        return stockRepository.findAll();
    }

    @RequestMapping(value = "/cal/saveAll")
    public String saveTradeCals(@RequestBody List<TradeCal> tradeCals)
    {
        tradeCalRepository.saveAll(tradeCals);
        return "calendars saved: " + DateUtil.now();
    }

    @RequestMapping(value = "/cal/max")
    public String getMaxDate()
    {
        TradeCal cal = tradeCalRepository.findTopOrderByDateDesc();
        if (cal == null)
            return "2019-01-01";
        return cal.getDate().toString();
    }
}
