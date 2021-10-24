package whzz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import whzz.pojo.TradeCal;

import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

@Service
public class TradeCalService
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<TradeCal> getOpenCalendars(Date startDate, Date endDate){
        StringBuilder sb = new StringBuilder("SELECT date FROM calendar WHERE 1 = 1 ");
        List<Object> objects = new ArrayList<Object>();
        if (startDate != null) {
            sb.append("AND date >= ? ");
            objects.add(startDate);
        }
        if (endDate != null) {
            sb.append("AND date <= ? ");
            objects.add(endDate);
        }
        sb.append("AND open = 1 ORDER BY date ASC");
        List<TradeCal> calendars = jdbcTemplate.query(sb.toString(), new TradeCal(), objects.toArray());
        return calendars;
    }

    public List<TradeCal> getOpenCalendarsBack(Date startDate, Date endDate){
        StringBuilder sb = new StringBuilder("SELECT date FROM calendar WHERE 1 = 1 ");
        List<Object> objects = new ArrayList<Object>();
        if (startDate != null) {
            sb.append("AND date >= ? ");
            objects.add(startDate);
        }
        if (endDate != null) {
            sb.append("AND date <= ? ");
            objects.add(endDate);
        }
        sb.append("AND open = 1 ORDER BY date DESC");
        List<TradeCal> calendars = jdbcTemplate.query(sb.toString(), new TradeCal(), objects.toArray());
        return calendars;
    }
}
