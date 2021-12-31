package whzz.client;

import cn.hutool.core.date.DateUtil;
import whzz.pojo.Daily;
import whzz.pojo.Stock;
import whzz.pojo.TradeCal;
import whzz.pojo.UpLimit;

import java.sql.Date;
import java.util.List;

public class QuantDataClientFeignHystrix implements QuantDataClient{
    @Override
    public String saveStocks(List<Stock> stocks) {
        return "stocks save failed: " + DateUtil.now();
    }

    @Override
    public Stock getStock(String code) {
        return null;
    }

    @Override
    public boolean stockExists(String code) {
        return false;
    }

    @Override
    public List<Stock> getAll() {
        return null;
    }

    @Override
    public String saveCals(List<TradeCal> cals) {
        return "calendars save failed: " + DateUtil.now();
    }

    @Override
    public String getCalMaxDate() {
        return "2019-01-01";
    }

    @Override
    public List<TradeCal> getOpenCals (String date)
    {
        return null;
    }

    @Override
    public Daily getDaily(String code, Date date) {
        return null;
    }

    @Override
    public String saveDailies (List<Daily> dailies)
    {
        return "dailies save failed: " + DateUtil.now();
    }

    @Override
    public String saveLimits (List<UpLimit> limits)
    {
        return "up limits save failed: " + DateUtil.now();
    }

    @Override
    public String saveLimit(UpLimit limit) {
        return null;
    }

    @Override
    public List<UpLimit> getLimits() {
        return null;
    }
}
