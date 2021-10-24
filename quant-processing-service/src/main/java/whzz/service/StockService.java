package whzz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import whzz.pojo.Stock;
import whzz.util.DateUtil;

import java.sql.Date;
import java.util.List;

@Service
public class StockService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Stock> getAllStocks()
    {
        String sql = "SELECT code, name, ipo_date, out_date FROM stock WHERE code LIKE 'sh.6%' OR code LIKE 'sz.0'";
        List<Stock> stocks = jdbcTemplate.query(sql, new Stock());
        return stocks;
    }

    public List<Stock> getStocksByDate(Date date)
    {
        String sql = "SELECT code, name, ipo_date, out_date FROM stock WHERE (code LIKE 'sh.6%' OR code LIKE 'sz.0%') ";
        sql += " AND ipo_date < ? AND (out_date > ? OR out_date IS NULL)";
        List<Stock> stocks = jdbcTemplate.query(sql, new Stock(), DateUtil.minusYear(date, 1), date);
        return stocks;
    }

    public Stock getStock(String code)
    {
        String sql = "SELECT code, name, ipo_date, out_date FROM stock WHERE code = ? ";
        Stock stock = jdbcTemplate.queryForObject(sql, new Stock(), code);
        return stock;
    }
}
