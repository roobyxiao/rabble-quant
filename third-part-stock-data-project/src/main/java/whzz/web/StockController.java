package whzz.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import whzz.pojo.Stock;
import whzz.pojo.Calendar;

import java.util.List;

@RestController
public class StockController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/stock")
    public List findStocks(){
        String sql = "SELECT symbol, name FROM stock_basic WHERE list_status = 'L' AND list_date < '2019-07-01' AND NOT EXISTS( SELECT * FROM stock_daily WHERE stock_basic.ts_code = stock_daily.ts_code AND stock_daily.high_limit / stock_daily.pre_close < 1.07 AND stock_daily.trade_date between '2019-07-01' and '2021-07-01')";
        List<Stock> list = jdbcTemplate.query(sql, new Stock());
        return list;
    }

    @GetMapping("/calender")
    public List findCalDate(){
        String sql = "select calendar_date, is_trading_day from trade_cal where calendar_date >= '2019-07-01' and is_trading_day = 1;";
        List<Calendar> list = jdbcTemplate.query(sql, new Calendar());
        return list;
    }
}
