package whzz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whzz.pojo.Daily;
import whzz.pojo.Stock;
import whzz.pojo.TradeCal;
import whzz.pojo.UPLimit;

import java.text.ParseException;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

@Service
public class QuantAnalyzeService {
    @Autowired
    private TradeCalService tradeCalService;
    @Autowired
    private StockService stockService;
    @Autowired
    private DailyService dailyService;
    @Autowired
    private UPLimitService upLimitService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void simulate() throws ParseException {
        List<UPLimit> limits = upLimitService.getAllUPLimits();
        for (UPLimit limit: limits) {
            Daily daily = dailyService.getDaily(limit.getCode(), limit.getDate());
            if (daily == null) {
                removeLimit(limit);
                continue;
            }
            if(daily.getClose() != daily.getHighLimit()) {
                removeLimit(limit);
                continue;
            }
            Stock stock = stockService.getStock(limit.getCode());
            Date ipoDate = stock.getIpoDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(ipoDate);
            calendar.add(Calendar.YEAR, 1);
            Date newDate = new Date(calendar.getTimeInMillis());
            if(newDate.after(limit.getDate())){
                removeLimit(limit);
                continue;
            }
            limit.setKeep(false);
            if(daily.getLow() == daily.getHighLimit())
                limit.setKeep(true);
            limit.setLast(1);
            saveLimit(limit);
        }
        updateLimitLast();
    }

    public void updateLimitLast()
    {
        List<TradeCal> cals = tradeCalService.getOpenCalendars(Date.valueOf("2018-01-01"), null);
        for(int i = 1; i < cals.size(); i++){
            List<UPLimit> limits = upLimitService.getUpLimitsByDate(cals.get(i).getDate());
            for (UPLimit limit: limits) {
                UPLimit lastLimit = upLimitService.getUPLimit(limit.getCode(), cals.get(i-1).getDate());
                if(lastLimit != null)
                    limit.setLast(lastLimit.getLast() + 1);
                saveLimit(limit);
            }
        }
    }

    @Transactional
    public void saveLimit(UPLimit limit)
    {
        String sql = "UPDATE up_limit set keep = ?, last = ? where code = ? and date = ?";
        jdbcTemplate.update(sql, limit.isKeep(), limit.getLast(), limit.getCode(), limit.getDate());
    }

    @Transactional
    public void removeLimit(UPLimit limit)
    {
        String sql = "DELETE FROM up_limit WHERE code = ? AND date = ?";
        jdbcTemplate.update(sql, limit.getCode(), limit.getDate());
    }
}
