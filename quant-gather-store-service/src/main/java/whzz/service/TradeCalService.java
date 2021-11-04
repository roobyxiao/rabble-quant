package whzz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import whzz.pojo.TradeCal;
import java.sql.Date;
import java.util.List;

@Service
public class TradeCalService
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<TradeCal> getTradeCals(Date date)
    {
        String sql = "SELECT date FROM calendar where open = 1 and date >= ? AND date <= NOW()";
        List<TradeCal> tradeCals = jdbcTemplate.query(sql, new TradeCal(), date);
        return tradeCals;
    }
}
