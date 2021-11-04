package whzz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whzz.pojo.Daily;
import whzz.pojo.Stock;

import java.util.List;

@Service
public class DailyService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveDailies(List<Daily> dailies)
    {
        for (Daily daily: dailies) {
            String insertSql = "INSERT INTO daily (code, date, open, high, low, close, pre_close, volume, amount, turn, trade_status, pct_chg, is_st) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(insertSql, daily.getCode(), daily.getDate(), daily.getOpen(), daily.getHigh(), daily.getLow(),
                    daily.getClose(), daily.getPreClose(), daily.getVolume(), daily.getAmount(), daily.getTurn(),
                    daily.isTradeStatus(), daily.getPctChg(), daily.isST());
        }
    }

    @Transactional
    public void updateDailies(List<Daily> dailies)
    {
        for (Daily daily: dailies) {
            String updateSql = "UPDATE daily SET high_limit = ?, low_limit = ? "
                    + "WHERE code = ? and date = ?";
            jdbcTemplate.update(updateSql, daily.getHighLimit(), daily.getLowLimit(), daily.getCode(), daily.getDate());
        }
    }
}
