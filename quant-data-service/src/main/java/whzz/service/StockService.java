package whzz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import whzz.pojo.Stock;
import whzz.util.DateUtil;

import java.util.Date;
import java.util.List;

@Service
public class StockService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Stock> getAllStocks()
    {
        String sql = "SELECT code, name, ipoDate, outDate FROM stock WHERE code LIKE 'sh.6%' OR code LIKE 'sz.0'";
        List<Stock> stocks = jdbcTemplate.query(sql, new Stock());
        return stocks;
    }

    public List<Stock> getStocksByDate(Date date)
    {
        String sql = "SELECT code, code_name, ipoDate, outDate FROM stock WHERE (code LIKE 'sh.6%' OR code LIKE 'sz.0%') ";
        sql += " AND ipoDate < ? AND (outDate > ? OR outDate IS NULL)";
        List<Stock> stocks = jdbcTemplate.query(sql, new Stock(), DateUtil.minusYear(date, 1), date);
        return stocks;
    }
}
