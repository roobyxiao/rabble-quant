package whzz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import whzz.pojo.Stock;
import java.util.List;

@Service
public class StockService
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Stock> getAllStocks()
    {
        String sql = "SELECT code, ipo_date FROM stock";
        List<Stock> stocks = jdbcTemplate.query(sql, new Stock());
        return stocks;
    }

    public Stock getStock(String code)
    {
        String sql = "SELECT code, ipo_date FROM stock where code = ?";
        Stock stock = jdbcTemplate.queryForObject(sql, Stock.class, code);
        return stock;
    }
}
