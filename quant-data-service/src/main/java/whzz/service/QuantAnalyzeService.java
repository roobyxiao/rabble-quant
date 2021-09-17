package whzz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whzz.pojo.Calendar;
import whzz.pojo.Daily;
import whzz.pojo.Limit;
import whzz.pojo.Stock;
import whzz.util.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class QuantAnalyzeService {
    @Autowired
    private CalendarService calendarService;
    @Autowired
    private StockService stockService;
    @Autowired
    private DailyService dailyService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public void simulate() throws ParseException {
        String querySql = "SELECT COUNT(1) FROM up_limit WHERE code = ? AND date = ?";
        List<Calendar> calendars = calendarService.getOpenCalendars(sdf.parse("2019-01-01"), null);
        Date startDate = calendars.get(0).getDate();
        List<Stock> stocks = stockService.getStocksByDate(startDate);
        List<Limit> limits = new ArrayList<Limit>();
        for (Stock stock: stocks) {
           limits = getBackLimits(stock.getCode(), startDate);
           if (!limits.isEmpty()) {
                limits.addAll(getFrontLimits(stock.getCode(), DateUtil.addDay(startDate, 1)));
                saveLimits(limits);
           }
        }
        calendars.remove(0);
        for (Calendar calender : calendars) {
            limits = new ArrayList<Limit>();
            stocks = stockService.getStocksByDate(calender.getDate());
            for (Stock stock: stocks) {
                Daily daily = dailyService.getDaily(stock.getCode(), calender.getDate());
                if (daily != null) {
                    if (daily.isUP()) {
                        int count = jdbcTemplate.queryForObject(querySql, Integer.class, daily.getCode(), daily.getDate());
                        if (count == 0) {
                            limits = getFrontLimits(stock.getCode(), calender.getDate());
                            saveLimits(limits);
                        }
                    }
                }
            }
        }
    }

    private List<Limit> getBackLimits(String code, Date date)
    {
        List<Limit> list = new ArrayList<Limit>();
        List<Calendar> calendars = calendarService.getOpenCalendarsBack(null, date);
        for (Calendar calendar: calendars) {
            Daily daily = dailyService.getDaily(code, calendar.getDate());
            if (daily == null)
                break;
            else  {
                if (!daily.isUP()) {
                    break;
                }
                Limit limit = new Limit(code, calendar.getDate(), 1, daily.keepUP(), 1);
                list.add(limit);
            }
        }
        Collections.reverse(list);
        return list;
    }

    private List<Limit> getFrontLimits(String code, Date date)
    {
        List<Limit> list = new ArrayList<Limit>();
        List<Calendar> calendars = calendarService.getOpenCalendars(date, null);
        for (Calendar calendar: calendars) {
            Daily daily = dailyService.getDaily(code, calendar.getDate());
            if (daily == null)
                break;
            else  {
                if (!daily.isUP()) {
                    break;
                }
                Limit limit = new Limit(code, calendar.getDate(), 1, daily.keepUP(), 1);
                list.add(limit);
            }
        }
        return list;
    }

    @Transactional
    public void saveLimits(List<Limit> limits)
    {
        String insertSql = "INSERT INTO up_limit (code, date, up_down, keep, days) VALUES (?, ?, ?, ?, ?)";
        for (int i = 0; i < limits.size(); i++) {
            Limit limit = limits.get(i);
            limit.setDays(i + 1);
            jdbcTemplate.update(insertSql, limit.getCode(), limit.getDate(), limit.getUpDown(), limit.isKeep(), limit.getDays());
        }
    }
}
