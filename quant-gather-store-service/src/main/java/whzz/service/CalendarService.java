package whzz.service;

import cn.hutool.core.collection.CollUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import whzz.pojo.Calendar;
import whzz.util.SpringContextUtil;

import java.util.List;

@Service
@CacheConfig(cacheNames = "calendar")
public class CalendarService {

    private List<Calendar> calendars;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Calendar> fresh(){
        String sql = "SELECT calendar_date FROM calendar WHERE is_trading_day = 1";
        calendars = jdbcTemplate.query(sql, new Calendar());
        CalendarService calendarService = SpringContextUtil.getBean(CalendarService.class);
        calendarService.remove();
        return calendarService.store();
    }

    @CacheEvict(allEntries = true)
    public void remove(){

    }
    @Cacheable(key="'calendars'")
    public List<Calendar> store(){
        System.out.println(this);
        return calendars;
    }
    @Cacheable(key="'calendars'")
    public List<Calendar> get(){
        return CollUtil.toList();
    }
}

