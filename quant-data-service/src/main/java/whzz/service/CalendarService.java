package whzz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import whzz.pojo.Calendar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CalendarService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Calendar> getOpenCalendars(Date startDate, Date endDate){
        StringBuilder sb = new StringBuilder("SELECT calendar_date FROM calendar WHERE 1 = 1 ");
        List<Object> objects = new ArrayList<Object>();
        if (startDate != null) {
            sb.append("AND calendar_date >= ? ");
            objects.add(startDate);
        }
        if (endDate != null) {
            sb.append("AND calendar_date <= ? ");
            objects.add(endDate);
        }
        sb.append("AND is_trading_day = 1 ORDER BY calendar_date ASC");
        List<Calendar> calendars = jdbcTemplate.query(sb.toString(), new Calendar(), objects.toArray());
        return calendars;
    }

    public List<Calendar> getOpenCalendarsBack(Date startDate, Date endDate){
        StringBuilder sb = new StringBuilder("SELECT calendar_date FROM calendar WHERE 1 = 1 ");
        List<Object> objects = new ArrayList<Object>();
        if (startDate != null) {
            sb.append("AND calendar_date >= ? ");
            objects.add(startDate);
        }
        if (endDate != null) {
            sb.append("AND calendar_date <= ? ");
            objects.add(endDate);
        }
        sb.append("AND is_trading_day = 1 ORDER BY calendar_date DESC");
        List<Calendar> calendars = jdbcTemplate.query(sb.toString(), new Calendar(), objects.toArray());
        return calendars;
    }
}
