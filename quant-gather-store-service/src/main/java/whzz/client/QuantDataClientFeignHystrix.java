package whzz.client;

import cn.hutool.core.date.DateUtil;
import org.springframework.stereotype.Component;
import whzz.pojo.Stock;
import whzz.pojo.TradeCal;

import java.util.List;

@Component
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
}
