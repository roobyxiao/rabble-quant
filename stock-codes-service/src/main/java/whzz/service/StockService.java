package whzz.service;

import cn.hutool.core.collection.CollUtil;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import whzz.pojo.Stock;

import java.util.List;

@Service
@CacheConfig(cacheNames = "indexes")
public class StockService {
    private List<Stock> indexes;

    @Cacheable(key="'all_codes'")
    public List<Stock> get(){
        Stock stock = new Stock();
        stock.setName("无效指数代码");
        stock.setCode("000000");
        return CollUtil.toList(stock);
    }
}
