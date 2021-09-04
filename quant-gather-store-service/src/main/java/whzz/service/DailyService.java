package whzz.service;

import cn.hutool.core.collection.CollUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import whzz.pojo.Daily;
import whzz.util.SpringContextUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@CacheConfig(cacheNames = "daily")
public class DailyService {
    private Map<String, List<Daily>> dailyMap = new HashMap<>();
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Daily> fresh(String code){
        String sql = "SELECT code, date , open, high, low, close, preclose, volume, isST, high_limit, low_limit FROM daily WHERE code = ? AND tradestatus = 1 ORDER BY date ASC ";
        List<Daily> dailies = jdbcTemplate.query(sql, new Object[]{code}, new Daily());
        dailyMap.put(code,dailies);

        DailyService dailyService = SpringContextUtil.getBean(DailyService.class);
        dailyService.remove(code);//清除缓存
        return dailyService.store(code);//为了加入缓存
    }

    @CachePut(key="'daily-'+ #p0")
    public List<Daily> store(String code) {
        return dailyMap.get(code);
    }

    @CacheEvict(key = "'daily-'+ #p0")
    public void remove(String code) {
    }

    @Cacheable(key="'daily-'+ #p0")
    public List<Daily> get(String code){
        return CollUtil.toList();
    }
}
