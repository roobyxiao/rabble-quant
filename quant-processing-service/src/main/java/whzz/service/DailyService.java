package whzz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import whzz.pojo.Daily;

import java.util.Date;
import java.util.List;

@Service
public class DailyService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Daily> getDailiesByCode(String code) {
        String sql = "SELECT code, date, open, high, low, close, pre_close, volume, is_st, high_limit, low_limit " +
                "FROM daily WHERE trade_status = 1 AND is_st = 0 AND code = ?";
        List<Daily> dailies = jdbcTemplate.query(sql, new Daily(), code);
        return dailies;
    }

    public Daily getDaily (String code, Date date) {
        String sql = "SELECT code, date, open, high, low, close, pre_close, volume, is_st, high_limit, low_limit " +
                "FROM daily WHERE trade_status = 1 AND is_st = 0 AND code = ? AND date = ?";
        List<Daily> dailies = jdbcTemplate.query(sql, new Daily(), code, date);
        return DataAccessUtils.singleResult(dailies);
    }
}
