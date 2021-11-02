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
        String sql = "SELECT code FROM stock";
        List<Stock> stocks = jdbcTemplate.query(sql, new Stock());
        return stocks;
    }
}
