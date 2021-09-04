package whzz.service;

import cn.hutool.core.collection.CollUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import whzz.pojo.Dividend;
import whzz.util.SpringContextUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@CacheConfig(cacheNames = "dividend")
public class DividendService {
    private Map<String, List<Dividend>> dividendMap = new HashMap<>();
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Dividend> fresh(String code){
        String sql = "SELECT code, dividend_date, ratio FROM dividend WHERE code = ? AND ratio IS NOT NULL ORDER BY dividend_date DESC ";
        List<Dividend> dividends = jdbcTemplate.query(sql, new Object[]{code}, new Dividend());
        dividendMap.put(code,dividends);

        DividendService dividendService = SpringContextUtil.getBean(DividendService.class);
        dividendService.remove(code);//清除缓存
        return dividendService.store(code);//为了加入缓存
    }

    @CachePut(key="'dividend-'+ #p0")
    public List<Dividend> store(String code) {
        return dividendMap.get(code);
    }

    @CacheEvict(key = "'dividend-'+ #p0")
    public void remove(String code) {
    }

    @Cacheable(key="'dividend-'+ #p0")
    public List<Dividend> get(String code){
        return CollUtil.toList();
    }
}
