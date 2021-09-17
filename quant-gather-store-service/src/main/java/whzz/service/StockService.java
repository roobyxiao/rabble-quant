package whzz.service;

import cn.hutool.core.collection.CollUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import whzz.pojo.Stock;
import whzz.util.SpringContextUtil;

import java.util.List;

@Service
@CacheConfig(cacheNames = "stock")
public class StockService {

    private List<Stock> stocks;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public StockService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Stock> fresh(){
        String sql = "SELECT code, code_name, ipoDate, outDate FROM stock WHERE type = 1 AND code NOT LIKE 'sz.3%'";
        stocks = jdbcTemplate.query(sql, new Stock());
        StockService stockService= SpringContextUtil.getBean(StockService.class);
        stockService.remove();
        return stockService.store();
    }

    @CacheEvict(allEntries = true)
    public void remove(){

    }
    @Cacheable(key="'stocks'")
    public List<Stock> store(){
        System.out.println(this);
        return stocks;
    }
    @Cacheable(key="'stocks'")
    public List<Stock> get(){
        return CollUtil.toList();
    }
}
