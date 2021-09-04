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
import whzz.pojo.Forward;
import whzz.util.SpringContextUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@CacheConfig(cacheNames = "forward")
public class ForwardService {
    private Map<String, List<Forward>> forwardMap = new HashMap<>();
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Daily> fresh(String code){
        String sql = "SELECT code, date , open, high, low, close, preclose, volume FROM forward WHERE code = ? AND tradestatus = 1 ORDER BY date DESC ";
        List<Forward> forwards = jdbcTemplate.query(sql, new Object[]{code}, new Forward());
        forwardMap.put(code,forwards);

        DailyService dailyService = SpringContextUtil.getBean(DailyService.class);
        dailyService.remove(code);//清除缓存
        return dailyService.store(code);//为了加入缓存
    }

    @CachePut(key="'forward-'+ #p0")
    public List<Forward> store(String code) {
        return forwardMap.get(code);
    }

    @CacheEvict(key = "'forward-'+ #p0")
    public void remove(String code) {
    }

    @Cacheable(key="'forward-'+ #p0")
    public List<Forward> get(String code){
        return CollUtil.toList();
    }
}
